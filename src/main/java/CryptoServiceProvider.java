import java.util.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.BadPaddingException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.binary.Base32;
import java.nio.charset.StandardCharsets;

public class CryptoServiceProvider {
	private static Cipher aesCipher;
	private static SecretKeyFactory skf;
	private static SecureRandom secure;

	private static final int HASHITERATIONS = 96000;
	private static final int KEYGENITERATIONS = 64000;
	private static final int KEYSIZE = 256;
    private static final int TWO_FACTOR_SECRET_KEY_SIZE = 10; // Fixed by Google Authenticator

	public static String b64encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] b64decode(String str) {
		return Base64.getDecoder().decode(str.trim());
	}

    public static String b32encode(byte[] bytes) {
        return new Base32().encodeToString(bytes);
    }

    public static byte[] b32decode(String str) {
        return new Base32().decode(str);
    }

	static {
		try {
			aesCipher = Cipher.getInstance("AES/CBC/PKCS5PADDING", "SunJCE");
			skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512", "SunJCE");
			secure = new SecureRandom();
		} catch (NoSuchAlgorithmException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		System.out.println("Crypto init successful");
	}

	public static byte[] getIV() {
		return aesCipher.getIV();
	}

	public static String encrypt(String s, String pass, byte[] salt) {
		byte[] cipherText = null;
		try {
			aesCipher.init(Cipher.ENCRYPT_MODE, genKey(pass, salt));
			cipherText = aesCipher.doFinal(s.getBytes());
		} catch (InvalidKeyException e) {
			System.err.println(e.getMessage());
			System.err.println("AES Encrypt Invalid key exception");
		} catch (IllegalBlockSizeException e) {
			System.err.println(e.getMessage());
			System.err.println("AES Encrypt Illegal block size exception");
		} catch (Exception e) {
			System.err.println("Exception raised while encrypting");
		}

		return b64encode(cipherText);
	}

	public static String decrypt(String cipherText, String pass, byte[] salt, byte[] iv) {
		byte[] decrypted = null;
		try {
			aesCipher.init(Cipher.DECRYPT_MODE, genKey(pass, salt), new IvParameterSpec(iv));
			decrypted = aesCipher.doFinal(b64decode(cipherText));
		} catch (InvalidKeyException e) {
			System.err.println("AES Decrypt Invalid key exception");
		} catch (IllegalBlockSizeException e) {
			System.err.println(e.getMessage());
			System.err.println("AES Decrypt Illegal block size exception");
		} catch (BadPaddingException e) {
			System.err.println(e.getMessage());
			System.err.println("Incorrect password used for decryption");
		} catch (Exception e) {
			System.err.println("Exception raised while decrypting");
            e.printStackTrace();
		}

        if (decrypted == null) return null;
		return new String(decrypted);
	}

	/**
	 * Generate a new salt everytime a user creates an account with SPAM
	 * Or changes/resets their master password
	 */
	public static byte[] getNewSalt() {
		byte[] temp = new byte[16];
		secure.nextBytes(temp);
		return temp;
	}

	/**
	 * Generate an AES key to encrypt a user's vault from their password.
	 */
	private static SecretKey genKey(String password, byte[] salt) {
		byte[] key = saltedHash(password, salt, KEYGENITERATIONS);
		SecretKey skey = new SecretKeySpec(key, "AES");
		return skey;
	}

	/**
	 * Generate a salted hash to store in the file for user authentication
	 */
	public static String genSaltedHash(String password, byte[] salt) {
		byte[] hash = saltedHash(password, salt, HASHITERATIONS);
		return b64encode(hash);
	}

    /**
     * Generate a single-request authentication key
     */
    public static String genRequestAuthKey() {
        byte[] temp = new byte[16]; // TODO: determine what size this should be
        secure.nextBytes(temp);
        return b64encode(temp);
    }

	private static byte[] saltedHash(String password, byte[] salt, int iter) {
		byte[] temp = null;
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt,
					iter, KEYSIZE);
			temp = skf.generateSecret(spec).getEncoded();
		} catch (InvalidKeySpecException ex) {
			System.err.println("InvalidKeySpec exception caught");
		}
		return temp;
	}


    /**
     * Generate secret key for two-factor authentication
     * Source: http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
     */
    public static String getNewTwoFactorSecretKey() {
        byte[] temp = new byte[TWO_FACTOR_SECRET_KEY_SIZE];
		secure.nextBytes(temp);
        return b32encode(temp);
    }

    /**
     * Verify that the specified code is valid for the user with the
     * specified two-factor authentication secret key.
     * Source: http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
     */
    public static boolean verifyTwoFactorCodeWithSecret(String secret, long code) {
        try {
            byte[] decodedSecret = b32decode(secret);
            long t = new Date().getTime() / TimeUnit.SECONDS.toMillis(30);

            // Window is used to check codes generated in the near past.
            // You can use this value to tune how far you're willing to go.
            int window = 3;
            for (int i = -window; i <= window; i++) {
                long hash = getTwoFactorCode(decodedSecret, t + i);

                if (hash == code) {
                    return true;
                }
            }

            // The validation code is invalid.
            return false;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            return false;
        }
    }

    // Source: http://thegreyblog.blogspot.com/2011/12/google-authenticator-using-it-in-your.html
    private static int getTwoFactorCode(byte[] key, long t)
            throws NoSuchAlgorithmException, InvalidKeyException {
        byte[] data = new byte[8];
        long value = t;
        for (int i = 8; i-- > 0; value >>>= 8) {
            data[i] = (byte) value;
        }

        SecretKeySpec signKey = new SecretKeySpec(key, "HmacSHA1");
        Mac mac = Mac.getInstance("HmacSHA1");
        mac.init(signKey);
        byte[] hash = mac.doFinal(data);

        int offset = hash[20 - 1] & 0xF;

        // We're using a long because Java hasn't got unsigned int.
        long truncatedHash = 0;
        for (int i = 0; i < 4; ++i) {
            truncatedHash <<= 8;
            // We are dealing with signed bytes:
            // we just keep the first byte.
            truncatedHash |= (hash[offset + i] & 0xFF);
        }

        truncatedHash &= 0x7FFFFFFF;
        truncatedHash %= 1000000;

        return (int) truncatedHash;
    }

}

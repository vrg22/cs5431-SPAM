import java.util.*;
import java.io.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

public class CryptoServiceProvider {
	private static Cipher aesCipher;
	private static SecretKeyFactory skf;
	private static SecureRandom secure;

	private static final int HASHITERATIONS = 64000;
	private static final int KEYGENITERATIONS = 96000;
	private static final int KEYSIZE = 256;

	public static String b64encode(byte[] bytes) {
		return Base64.getEncoder().encodeToString(bytes);
	}

	public static byte[] b64decode(String str) {
		return Base64.getDecoder().decode(str.trim());
	}

	public CryptoServiceProvider() {
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

	public byte[] getIV() {
		return aesCipher.getIV();
	}

	public String encrypt(String s, String pass, byte[] salt) {
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

	public String decrypt(String cipherText, String pass, byte[] salt, byte[] iv) {
		byte[] decrypted = null;
		try {
			aesCipher.init(Cipher.DECRYPT_MODE, genKey(pass, salt), new IvParameterSpec(iv));
			decrypted = aesCipher.doFinal(b64decode(cipherText));
		} catch (InvalidKeyException e) {
			System.err.println("AES Decrypt Invalid key exception");
		} catch (IllegalBlockSizeException e) {
			System.err.println(e.getMessage());
			System.err.println("AES Decrypt Illegal block size exception");
		} catch (Exception e) {
			System.err.println("Exception raised while decrypting");
            e.printStackTrace();
		}

		return new String(decrypted);
	}

	/**
	 * Generate a new salt everytime a user creates an account with SPAM
	 * Or changes/resets their master password
	 */
	public byte[] getNewSalt() {
		byte[] temp = new byte[16];
		secure.nextBytes(temp);
		return temp;
	}

	/**
	 * Generate an AES key to encrypt a user's vault from their password.
	 */
	private SecretKey genKey(String password, byte[] salt) {
		byte[] key = saltedHash(password, salt, KEYGENITERATIONS);
		SecretKey skey = new SecretKeySpec(key, "AES");
		if (skey == null) {
			System.out.println("skey null");
		}
		return skey;
	}

	/**
	 * Generate a salted hash to store in the file for user authentication
	 */
	public String genSaltedHash(String password, byte[] salt) {
		byte[] hash = saltedHash(password, salt, HASHITERATIONS);
		return b64encode(hash);
	}

	private byte[] saltedHash(String password, byte[] salt, int iter) {
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
}

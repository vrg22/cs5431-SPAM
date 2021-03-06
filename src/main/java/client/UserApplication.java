import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class UserApplication extends ClientApplication
{
	public static final String USER_TYPE = "user";

	private XMLStorageController store;

    private int userId; // User ID of currently logged-in user
    private UserStorageFile userVault; // Vault of currently logged-in user
    private byte[] userSalt;
    private String master;
    private String authKey;
    private UserFrame frame;

	public UserApplication() {
        gson = new Gson();
        store = new XMLStorageController("users");
		frame = new UserFrame(this);
        frame.start();
	}

	public static void main(String args[])
	{
		new UserApplication();
	}

	/**
	 * Attempt to log in with specified credentials
	 *
	 * @return Was login successful
	 */
    public boolean login(String email, String password, String twoFactorCode) {
        // Request server for salt with user's email
        Map<String, String> saltParams = new HashMap<>();
        saltParams.put("type", USER_TYPE);
        saltParams.put("email", email);
        String saltResponseJson;
        try {
            saltResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/salt",
                saltParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        SaltResponse saltResponse = gson.fromJson(saltResponseJson,
            SaltResponse.class);
        if (saltResponse == null) return false;
        byte[] salt = saltResponse.getSalt();

        String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);

        String initialAuthKey = CryptoServiceProvider.genRequestAuthKey();

        // Request user for user's ID, IV, and encrypted vault
        Map<String, String> authParams = new HashMap<>();
        authParams.put("type", USER_TYPE);
        authParams.put("email", email);
        authParams.put("master", saltedHash);
        authParams.put("twoFactorCode", twoFactorCode);
        authParams.put("nextAuthKey", initialAuthKey);
        String authResponseJson;
        try {
            authResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/auth",
                authParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        AuthResponse authResponse = gson.fromJson(authResponseJson,
            AuthResponse.class);
        if (authResponse == null) return false; // Failed login

        int id = authResponse.getId();
        String encVault = authResponse.getVault();
        byte[] iv = authResponse.getIV();

        String userVaultStr = CryptoServiceProvider.decrypt(encVault, password, salt, iv);
        try {
            FileInputStream tmpStream = null;
            try {
                PrintWriter tmpWriter = new PrintWriter(".tmpvault");
                tmpWriter.println(userVaultStr);
                tmpWriter.close();
                tmpStream = new FileInputStream(".tmpvault");
                userVault = store.readFileForUser(tmpStream);
                tmpStream.close();
            } catch (Exception e) {
                System.err.println("Problem logging in");
                return false;
            } finally {
                if (tmpStream != null) tmpStream.close();
                File f = new File(".tmpvault");
                if (f.exists() && !f.isDirectory()) {
                    f.delete();
                }
            }
        } catch (IOException e) {
            System.err.println("Problem logging in");
            return false;
        }

        // Successful login
        userId = authResponse.getId();
        userSalt = salt;
        master = password;
        authKey = initialAuthKey;

        return true;
	}

    public void logout(boolean expired) {
        userId = -1;
        userVault = null;
        frame.setPanel(new UserLoginPanel(expired));
    }

    /**
     * Register new user, and log in with the new user.
     *
     * @return Was user successfully registered
     */
    public boolean register(String email, String password, String recovery,
            String twoFactorSecret) {
        byte[] salt = CryptoServiceProvider.getNewSalt();
        String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);
        String recoveryHash = CryptoServiceProvider.genSaltedHash(recovery, salt);

        StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?
        store.setupUserXML(xmlStringBuilder, 0); // TODO: set user ID (or get rid of in user XML files?)
        String decryptedVault = xmlStringBuilder.toString();
        String encVault = CryptoServiceProvider.encrypt(decryptedVault, password, salt);
        byte[] iv = CryptoServiceProvider.getIV();

		String encPass = CryptoServiceProvider.encrypt(password, recovery, salt);
        byte[] recoverIV = CryptoServiceProvider.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("type", USER_TYPE);
        params.put("email", email);
        params.put("salt", CryptoServiceProvider.b64encode(salt));
        params.put("saltedHash", saltedHash);
        params.put("vault", encVault);
        params.put("iv", CryptoServiceProvider.b64encode(iv));
        params.put("encryptedPass", encPass);
        params.put("reciv", CryptoServiceProvider.b64encode(recoverIV));
        params.put("recoveryHash", recoveryHash);
        params.put("twoFactorSecret", twoFactorSecret);

        String responseJson;
        try {
            responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);

        return response != null && response.success();
    }

    /**
     * Obliterate logged-in user
     *
     * @return Was user successfully obliterated
     */
    public boolean obliterateUser() {
        Map<String, String> params = new HashMap<>();
        params.put("authKey", authKey);
        String nextAuthKey = CryptoServiceProvider.genRequestAuthKey();
        params.put("nextAuthKey", nextAuthKey);

        String responseJson;
        try {
            responseJson = SendHttpsRequest.delete(HTTPS_ROOT
                + "/users/" + userId, null);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        ObliterateResponse response = gson.fromJson(responseJson, ObliterateResponse.class);

        if (response.success()) {
            authKey = nextAuthKey;
            logout(false);

            return true;
        } else {
            logout(true);
            return false;
        }
    }

    /**
     * Get list of accounts associated with logged-in user
     *
     * @return Array of account headers for user's accounts
     */
    public Account.Header[] getAccounts() {
        if (userVault == null) return new Account.Header[]{};

        return userVault.getAccountHeaders();
    }

    /**
     * Store new account for logged-in user
     *
     * @return Was account successfully stored
     */
    public boolean storeNewAccount(String name, String username,
            String password) {
        if (userVault == null) return false;

        int newAccountId = Integer.parseInt(userVault.getNextAccountID());
        Account account = new Account(newAccountId, name, username, password);
        userVault.putAccount(account);

        return saveVault();
    }

    /**
     * Get details for an account
     *
     * @return Account details for account with specified account ID
     */
    public Account getAccount(int accountId) {
        if (userVault == null) return null;

        return userVault.getAccountWithId(accountId);
    }

    /**
     * Update a stored account
     *
     * @return Was account successfully updated
     */
    public boolean updateAccount(Account account) {
        if (userVault == null) return false;

        return userVault.updateAccount(account) && saveVault();
    }

    /**
     * Delete a stored account
     *
     * @return Was account successfully deleted
     */
    public boolean deleteAccount(int accountId) {
        if (userVault == null) return false;

        return userVault.deleteAccountWithId(accountId) && saveVault();
    }

    private boolean saveVault() {
        String decryptedVault;
        try {
            FileOutputStream out = null;
            FileInputStream in = null;
            try {
                out = new FileOutputStream(new File(".tmpvault"));
                store.writeFileToStream(userVault, out);
                out.close();

                in = new FileInputStream(new File(".tmpvault"));
                StringBuilder builder = new StringBuilder();
                int ch;
                while((ch = in.read()) != -1){
                    builder.append((char)ch);
                }
                in.close();
                decryptedVault = builder.toString();
            } catch (IOException e) {
                return false;
            } finally {
                if (out != null) out.close();
                if (in != null) in.close();
                File f = new File(".tmpvault");
                if (f.exists() && !f.isDirectory()) {
                    f.delete();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String encVault = CryptoServiceProvider.encrypt(decryptedVault, master, userSalt);
        byte[] iv = CryptoServiceProvider.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("type", USER_TYPE); //TODO: Check!
        params.put("vault", encVault);
        params.put("iv", CryptoServiceProvider.b64encode(iv));
        params.put("authKey", authKey);
        String nextAuthKey = CryptoServiceProvider.genRequestAuthKey();
        params.put("nextAuthKey", nextAuthKey);

        String responseJson;
        try {
            responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/users/"
                + userId + "/save", params);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        SaveResponse response = gson.fromJson(responseJson, SaveResponse.class);
        authKey = nextAuthKey;

        return response.success();
    }

    public boolean resetPass(String email, String curPass, String twoFactorCode,
            String newPass, String recovery) {
        if (login(email, curPass, twoFactorCode)) {
            master = newPass;
            if (saveVault()) {
              String saltedHash = CryptoServiceProvider.genSaltedHash(newPass, userSalt);
              String encPass = CryptoServiceProvider.encrypt(newPass, recovery, userSalt);
              byte[] recoverIV = CryptoServiceProvider.getIV();

              Map<String, String> params = new HashMap<>();
              params.put("saltedHash", saltedHash);
              params.put("encryptedPass", encPass);
              params.put("reciv", CryptoServiceProvider.b64encode(recoverIV));
              params.put("authKey", authKey);
              String nextAuthKey = CryptoServiceProvider.genRequestAuthKey();
              params.put("nextAuthKey", nextAuthKey);
              System.err.println("resetPass authKey:" +authKey);

              String responseJson;
              try {
                responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/users/"
                    + userId + "/resetpass", params);
              } catch (IOException e) {
                System.out.println("Problem connecting to server.");
                return false;
              }
              SaveResponse response = gson.fromJson(responseJson, SaveResponse.class);

              if (response.success()) {
                authKey = nextAuthKey;
                System.out.println("resetPass responded true");
                return true;
              } else {
                System.out.println("resetPass responded false");
                logout(true);
                return false;
              }
            }
            return false;
        } else {
            return false;
        }
    }

    public boolean recoverPass(String email, String recovery, String twoFactorCode,
            String newPass) {
        Map<String, String> saltParams = new HashMap<>();
        saltParams.put("type", USER_TYPE);
        saltParams.put("email", email);
        String saltResponseJson;
        try {
            saltResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/salt",
                saltParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        SaltResponse saltResponse = gson.fromJson(saltResponseJson,
            SaltResponse.class);
        if (saltResponse == null) return false;
        byte[] salt = saltResponse.getSalt();

        String saltedRecovery = CryptoServiceProvider.genSaltedHash(recovery, salt);

        // Request user for user's ID, IV, and encrypted vault
        Map<String, String> recoParams = new HashMap<>();
        recoParams.put("type", USER_TYPE);
        recoParams.put("email", email);
        recoParams.put("recovery", saltedRecovery);
        recoParams.put("twoFactorCode", twoFactorCode);

        String recoResponseJson;
        try {
            recoResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/recover",
                recoParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }

        RecoResponse recoResponse = gson.fromJson(recoResponseJson,
            RecoResponse.class);
        if (recoResponse == null) return false; // Failed recovery

        String encPass = recoResponse.getEncPass();
        byte[] iv = recoResponse.getIV();

        String password = CryptoServiceProvider.decrypt(encPass, recovery, salt, iv);

        if (resetPass(email, password, twoFactorCode, newPass, recovery)) {
            System.err.println("Password recovery successful");
        }

        return true;
	}
}

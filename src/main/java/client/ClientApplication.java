import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import java.io.*;

public class ClientApplication
{
	public static final String HTTPS_ROOT = "https://spam3.kevinmbeaulieu.com:5000";

	private static final int PASSWORD_LENGTH = 12;
    private Gson gson;
    private CryptoServiceProvider crypto;
    private XMLStorageController store;

    private int userId; // User ID of currently logged-in user
    private UserStorageFile userVault; // Vault of currently logged-in user
    private byte[] userSalt;
    private String master;

	public ClientApplication() {
        gson = new Gson();
        crypto = new CryptoServiceProvider();
        store = new XMLStorageController("users");
		new ClientFrame(this).start();
	}

	public static void main(String args[])
	{
		new ClientApplication();
	}

	/**
	 * Attempt to log in with specified credentials
	 *
	 * @return Was login successful
	 */
	public boolean login(String email, String password) {
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        String responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/login", params);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);
        if (response == null) return false;

        byte[] salt = response.getSalt();
        String saltedHash = crypto.genSaltedHash(password, salt);

        System.out.println("trying to log in");
        System.out.println("password: '"+password+"', salt: '"+crypto.b64encode(salt)+"'");
        System.out.println("saltedhash: '"+saltedHash+"'");
        System.out.println("storedhash: '"+response.getSaltedHash()+"'");

        if (saltedHash.equals(response.getSaltedHash())) {
            // Success
            String encVault = response.getVault();
            byte[] iv = response.getIV();
            String userVaultStr = crypto.decrypt(encVault, password, salt, iv);

            try {
                FileInputStream tmpStream = null;
                try {
                    PrintWriter tmpWriter = new PrintWriter(".tmpvault");
                    tmpWriter.println(userVaultStr);
                    tmpWriter.close();
                    tmpStream = new FileInputStream(".tmpvault");
                    userVault = store.readFileForUser(tmpStream);
                    tmpStream.close();
                } catch (IOException e) {
                    return false;
                } finally {
                    if (tmpStream != null) tmpStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            userId = response.getId();
            userSalt = salt;
            master = password;

            return true;
        } else {
            // Incorrect email and/or password
            return false;
        }
	}

    public void logout() {
        userId = -1;
        userVault = null;
    }

    /**
     * Register new user, and log in with the new user.
     *
     * @return Was user successfully registered
     */
    public boolean register(String email, String password) {
        byte[] salt = crypto.getNewSalt();
        String saltedHash = crypto.genSaltedHash(password, salt);

        StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?
        store.setupUserXML(xmlStringBuilder, 0); // TODO: set user ID (or get rid of in user XML files?)
        String decryptedVault = xmlStringBuilder.toString();
        String encVault = crypto.encrypt(decryptedVault, password, salt);
        byte[] iv = crypto.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("salt", CryptoServiceProvider.b64encode(salt));
        params.put("saltedHash", saltedHash);
        params.put("vault", encVault);
        params.put("iv", CryptoServiceProvider.b64encode(iv));

        String responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);

        if (response != null && response.success()) {
            login(email, password);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Obliterate logged-in user
     *
     * @return Was user successfully obliterated
     */
    public boolean obliterateUser() {
        String responseJson = SendHttpsRequest.delete(HTTPS_ROOT
            + "/users/" + userId);
        ObliterateResponse response = gson.fromJson(responseJson, ObliterateResponse.class);

        if (response.success()) {
            logout();

            return true;
        } else {
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

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
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
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        String encVault = crypto.encrypt(decryptedVault, master, userSalt);
        byte[] iv = crypto.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("vault", encVault);
        params.put("iv", CryptoServiceProvider.b64encode(iv));

        String responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/users/"
            + userId + "/save", params);
        SaveResponse response = gson.fromJson(responseJson, SaveResponse.class);

        return response.success();
    }
}

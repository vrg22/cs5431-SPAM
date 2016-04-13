import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import java.io.*;

public class ClientApplication
{
	public static final String HTTPS_ROOT = "https://spam3.kevinmbeaulieu.com";
    // public static final String HTTPS_ROOT = "localhost:5000";

	private static final int PASSWORD_LENGTH = 12;
    private Gson gson;
    private CryptoServiceProvider crypto;
    private XMLStorageController store;

    private int userId; // User ID of currently logged-in user
    private UserStorageFile userVault; // Vault of currently logged-in user

	public ClientApplication() {
        gson = new Gson();
        crypto = new CryptoServiceProvider();
        store = new XMLStorageController();
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

        byte[] salt = response.getSalt();
        String saltedHash = crypto.genSaltedHash(password, salt);

        if (saltedHash.equals(response.getSaltedHash())) {
            // Success
            String encVault = response.getVault();
            byte[] iv = response.getIV().getBytes();
            String userVaultStr = crypto.decrypt(encVault, password, salt, iv);

            try {
                PrintWriter tmpWriter = new PrintWriter(".tmpvault");
                tmpWriter.println(userVaultStr);
                tmpWriter.close();
                FileInputStream tmpStream = new FileInputStream(".tmpvault");
                userVault = store.readFileForUser(tmpStream);
            } catch (FileNotFoundException e) {
                return false;
            }

            userId = response.getId();

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

        Map<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("salt", new String(salt));
        params.put("saltedHash", saltedHash);
        params.put("vault", xmlStringBuilder.toString());
        System.out.println("email: "+email);
        System.out.println("salt: "+new String(salt));
        System.out.println("saltedHash: "+saltedHash);
        System.out.println("vault: "+xmlStringBuilder.toString());

        String responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);
        System.out.println("json: "+responseJson);
        System.out.println("response: "+response);
        if (response.success()) {
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
        if (userVault == null) return null;

        return userVault.getAccountHeaders();
    }

    /**
     * Store new account for logged-in user
     *
     * @return Was account successfully stored
     */
    public boolean storeNewAccount(String name, String username,
            String password) {
        // TODO: implement
        return false;
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
        // TODO: implement
        return false;
    }

    /**
     * Delete a stored account
     *
     * @return Was account successfully deleted
     */
    public boolean deleteAccount(int accountId) {
        if (userVault == null) return false;

        return userVault.deleteAccountWithId(accountId);
    }

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
    }
}

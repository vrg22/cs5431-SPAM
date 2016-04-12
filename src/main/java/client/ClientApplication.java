import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;

public class ClientApplication
{
	public static final String HTTPS_ROOT = "https://spam3.kevinmbeaulieu.com";

	private static final int PASSWORD_LENGTH = 12;
	private int userId; // User ID of currently logged-in user
    private Gson gson;
    private CryptoServiceProvider crypto;

	public ClientApplication() {
        gson = new Gson();
        crypto = new CryptoServiceProvider();
		new ClientFrame().start();
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
	public static boolean login(String email, String password) {
		// TODO: implement. If successful, set userId field
        Map<String, String> params = new HashMap<>();
        params.put("email", email);

        String responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/login", params);
        LoginResponse response = gson.fromJson(responseJson, LoginResponse.class);

        byte[] salt = response.getSalt();
        String saltedHash = crypto.genSaltedHash(password, salt);

        if (saltedHash.equals(response.getSaltedHash())) {
            // Success
        } else {
            // Incorrect email and/or password
        }
	}

    /**
     * Register new user
     *
     * @return Was user successfully registered
     */
    public static boolean register(String email, String password) {
        // TODO: implement
        return false;
    }

    /**
     * Obliterate logged-in user
     *
     * @return Was user successfully obliterated
     */
    public static boolean obliterateUser() {
        // TODO: implement
        return false;
    }

    /**
     * Get list of accounts associated with logged-in user
     *
     * @return Array of account headers for user's accounts
     */
    public static Account.Header[] getAccounts() {
        // TODO: implement
        return null;
    }

    /**
     * Store new account for logged-in user
     *
     * @return Was account successfully stored
     */
    public static boolean storeNewAccount(String name, String username,
            String password) {
        // TODO: implement
        return false;
    }

    /**
     * Get details for an account
     *
     * @return Account details for account with specified account ID
     */
    public static Account getAccount(int accountId) {
        // TODO: implement
        return null;
    }

    /**
     * Update a stored account
     *
     * @return Was account successfully updated
     */
    public static boolean updateAccount(Account account) {
        // TODO: implement
        return false;
    }

    /**
     * Delete a stored account
     *
     * @return Was account successfully deleted
     */
    public static boolean deleteAccount(int accountId) {
        // TODO: implement
        return false;
    }

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public static String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
    }
}

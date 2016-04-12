import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class ClientApplication
{
    public static final String HTTPS_ROOT = "https://spam3.kevinmbeaulieu.com";

    private PasswordGenerator passwordGenerator;
    private static final int PASSWORD_LENGTH = 12;

    private int userId; // User ID of currently logged-in user

    public ClientApplication() {
        passwordGenerator = new ComplexPasswordGenerator();

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
        return false;
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
     * Obliterate user
     *
     * @return Was user successfully obliterated
     */
    public static boolean obliterateUser(int userId) {
        // TODO: implement
        return false;
    }

    /**
     * Get list of accounts associated with user
     *
     * @return Array of account headers for user's accounts
     */
    public static Account.Header[] getAccounts(int userId) {
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
        // TODO: implement
        return passwordGenerator.next(PASSWORD_LENGTH);
    }
}

import java.util.logging.*;
import java.io.*;

// Provides public methods to complete user-level actions
public class CentralServerController implements ServerController {
	private Logger logger;
    private StorageController store;

    public CentralServerController() {
        //Set up logging
        try {
            String loggerName = CentralServerController.class.getName();
            logger = SimpleLogger.getLogger(loggerName);
            logger.info("Starting up SPAM...");
        } catch (SecurityException e) {
            System.out.println("Could not start logger.");
            throw e;
        }

        // Set up storage
        store = new XMLStorageController(); // TODO: make sure this is the proper initialization
        store.createPasswordsFileOnStream(store.getPasswordsOutput());
    }

    /**
     * Checks whether the specified username/password combination
     * matches an existing user.
     * @return user ID of matching user (-1 if invalid combination)
     */
    public int login(String username, String master) {
        PasswordStorageFile passwordFile = store.readPasswordsFile(store.getPasswordsInput());
        PasswordStorageEntry entry = passwordFile.getWithUsername(username);

        if (entry == null) {
            // No user with that username exists
            logger.warning("Attempt was made to log into nonexistent username " + username);
            return -1;
        }

        String hashedMaster = null;//hash(master); // TODO: implement hash
        if (!hashedMaster.equals(entry.getMaster())) {
            // Incorrect password
            logger.warning("Attempt was made to log into username " + username + " with incorrect password " + master);
            return -1;
        }

        logger.info("User " + entry.getUserId() + " successfully logged in.");

        return entry.getUserId();
    }

    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String master) {
        PasswordStorageFile passwordFile = store.readPasswordsFile(store.getPasswordsInput());

        if (passwordFile.contains("username", username)) {
            // User with that username already exists
            logger.warning("Attempt was made to register a new user with existing username " + username);
            return null;
        }

        int newUserId = Integer.parseInt(passwordFile.getNextID());  // TODO: pick unique user ID (Q: Need to be random?)
        User newUser = new User(username, master, newUserId); // TODO: should password be hashed or in plaintext here?
        PasswordStorageEntry newUserEntry =
            new PasswordStorageEntry(newUser); // TODO: password should be hashed here
        passwordFile.put(newUserEntry);

        UserStorageFile userFile = new UserStorageFile(newUserId);

        store.writeFileToStream(passwordFile, store.getPasswordsOutput());
        store.writeFileToStream(userFile, store.getOutputForUser(newUserId));

        logger.info("New user " + newUserId + " successfully registered.");

        return newUser;
    }

    /**
     * Attempts to obliterate a user's entire account.
     * @param userId ID of user to obliterate
     * @return "Was user's account successfully obliterated?"
     */
    public boolean obliterateUser(int userId) {
        PasswordStorageFile passwordFile = store.readPasswordsFile(store.getPasswordsInput());

        if (!passwordFile.removeWithUserId(""+userId)) {
            // No such user
            logger.warning("Attempt was made to obliterate nonexistent user " + userId);
            return false;
        }

        // TODO: delete user's file too

        store.writeFileToStream(passwordFile, store.getPasswordsOutput());

        logger.info("User " + userId + " successfully obliterated.");

        return true;
    }

    /**
     * Attempts to update an existing user.
     * Specified user must have an ID which matches an
     * existing user.
     * @param user updated version of the user
     * @return "Was user successfully updated?"
     */
    public boolean updateUser(User user) {
        PasswordStorageFile passwordFile = store.readPasswordsFile(store.getPasswordsInput());

        if (!passwordFile.removeWithUserId(""+user.getID())) {
            // No such user
            logger.warning("Attempt was made to update nonexistent user " + user.getID());
            return false;
        }

        passwordFile.putUser(user);

        store.writeFileToStream(passwordFile, store.getPasswordsOutput());

        logger.info("User " + user.getID() + " successfully updated.");

        return true;
    }

    /**
     * Returns a list of a user's stored accounts.
     * @param userId ID of user whose accounts to fetch
     * @return array of account headers corresponding to
     *      user's stored accounts (null if no stored accounts)
     */
    public Account.Header[] getAccountsForUser(int userId) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            return null;
        }

        return userFile.getAccountHeaders();
    }

    /**
     * Returns full details for a stored accountId
     * @param accountId ID of account to fetch
     * @return full description of specified accountId (null if does not exist)
     */
    public Account getDetailsForAccount(int userId, int accountId) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            return null;
        }

        return userFile.getAccountWithId(accountId);
    }

    /**
     * Attempts to store a new account for a user.
     * Specified user ID must match an existing user.
     * @return the account just created (null if unsuccessful)
     */
    public Account storeNewAccountForUser(int userId, String name,
            String username, String password) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("Attempt was made to store a new account for nonexistent user " + userId);
            return null;
        }

        int newAccountId = Integer.parseInt(userFile.getNextAccountID()); // TODO: get unique account ID (Q: Need to be random?)
        Account newAccount = new Account(newAccountId, /*userId,*/ name, username,
            password);

        store.writeFileToStream(userFile, store.getOutputForUser(userId));

        logger.info("New account " + newAccountId + " successfully stored for user " + userId);

        return newAccount;
    }

    /**
     * Attempts to update an existing account.
     * Specified account must have an ID which matches an
     * existing user.
     * @param account updated version of the account
     * @return "Was account successfully updated?"
     */
    public boolean updateAccount(int userId, Account account) {
        //UserStorageFile userFile = store.readFileForUser(getInputForUser(account.getUserID()));
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("Attempt was made to update account " + account.getID() + " for nonexistent user " + userId);
            return false;
        }

        if (!userFile.deleteAccountWithId(account.getID())) {
            // No such account existed
            logger.warning("Attempt was made to update nonexistent account " + account.getID() + " for user " + userId);
            return false;
        }

        userFile.putAccount(account);

        store.writeFileToStream(userFile, store.getOutputForUser(userId));

        logger.info("Account " + account.getID() + " for user " + userId + " successfully updated.");

        return true;
    }

    /**
     * Attempts to delete an account from a user's vault.
     * @param accountId ID of account to be deleted
     * @return "Was account successfully deleted?"
     */
    public boolean deleteAccount(int accountId, int userId) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("Attempt was made to delete account " + accountId + " for nonexistent user " + userId);
            return false;
        }

        if (!userFile.deleteAccountWithId(accountId)) {
            // No such account
            logger.warning("Attempt was made to delete nonexistent account " + accountId + " for user " + userId);
            return false;
        }

        store.writeFileToStream(userFile, store.getOutputForUser(userId));

        logger.info("Account " + accountId + " for user " + userId + " successfully deleted.");

        return true;
    }

    /**
     * Checks whether the specified account exists and
     * is tied to the specified user.
     * @return "Is specified account tied to specified user?"
     */
    public boolean isAccountForUser(int accountId, int userId) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            return false;
        }

        return userFile.containsAccountWithId(accountId);
    }

}

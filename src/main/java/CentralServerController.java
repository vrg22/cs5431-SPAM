import java.util.logging.*;

import org.w3c.dom.Document;

import java.io.*;

// Provides public methods to complete user-level actions
public class CentralServerController implements ServerController {
	public Logger logger;
    private StorageController store;
	private CryptoServiceProvider crypto;

    public CentralServerController(String logLocation) throws SecurityException, IOException {
        //Set up logging
        try {
            String loggerName = CentralServerController.class.getName();
            logger = Logger.getLogger(loggerName);
            //logger = SimpleLogger.getLogger(loggerName);   //TODO: This didn't work, do we want SimpleLogger?

            FileHandler fh = new FileHandler(logLocation, true);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            logger.info("Starting up SPAM...");
        } catch (SecurityException | IOException e) {
            System.out.println("Could not start logger.");
            throw e;
        }

        // Set up storage
        store = new XMLStorageController();
        store.createPasswordsFileOnStream(store.getPasswordsOutput());

		// Set up the crypto module
		crypto = new CryptoServiceProvider();
    }

    /**
     * Checks whether the specified username/password combination
     * matches an existing user.
     * @return user ID of matching user (-1 if invalid combination)
     */
    // public int login(String username, String master, String clientIp,
    //         PasswordStorageFile passwordFile) {
    //     PasswordStorageEntry entry = passwordFile.getWithUsername(username);
    //
    //     if (entry == null) {
    //         // No user with that username exists
    //         logger.warning("[IP=" + clientIp + "] Attempt was made to log "
    //             + "into nonexistent username " + username + ".");
    //         return -1;
    //     }
    //
	// 	byte[] userSalt = entry.getSalt();
    //     String hashedMaster = crypto.genSaltedHash(master, userSalt);
    //     if (!hashedMaster.equals(entry.getMaster())) {
    //         // Incorrect password
    //         logger.warning("[IP=" + clientIp + "] Attempt was made to log into "
    //             + "username " + username + " with incorrect password.");
    //         return -1;
    //     }
    //
    //     logger.info("[IP=" + clientIp + "] User " + entry.getUserId()
    //         + " successfully logged in.");
    //
    //     return entry.getUserId();
    // }

    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String userSalt,
            String saltedHash, String vault, String clientIp,
            PasswordStorageFile passwordFile) {

        if (passwordFile.contains("username", username)) {
            // User with that username already exists
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "register a new user with existing username " + username + ".");
            return null;
        }

        // Add user to main password file
        int newUserId = Integer.parseInt(passwordFile.getNextID());  // TODO: pick unique user ID (Q: Need to be random?)
        byte[] userIV = new byte[1];

        System.out.println("SERVER REGISTERING USER WITH SALT " + userSalt + " HASH " + saltedHash);
        User newUser = new User(username, userSalt.getBytes(), saltedHash, newUserId, userIV);
        PasswordStorageEntry newUserEntry = new PasswordStorageEntry(newUser);
        passwordFile.put(newUserEntry);

        UserStorageFile userFile = new UserStorageFile(newUserId);


        store.writeFileToDisk(passwordFile);

		// Create new user vault file
		store.writeEncryptedUserFileToDisk(newUserId, vault);

        logger.info("[IP=" + clientIp + "] New user " + newUserId
            + " successfully registered.");
        return newUser;
    }

    /**
     * Attempts to obliterate a user's entire account.
     * @param userId ID of user to obliterate
     * @return "Was user's account successfully obliterated?"
     */
    public boolean obliterateUser(int userId, String clientIp,
            PasswordStorageFile passwordFile) {

        if (!passwordFile.removeWithUserId(""+userId)) {
            // No such user
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "obliterate nonexistent user " + userId + ".");
            return false;
        }

        // TODO: delete user's file too

        store.writeFileToDisk(passwordFile);
        //store.writeFileToStream(passwordFile, store.getPasswordsOutput());

        logger.info("[IP=" + clientIp + "] User " + userId
            + " successfully obliterated.");

        return true;
    }

    /**
     * Attempts to update an existing user.
     * Specified user must have an ID which matches an
     * existing user.
     * @param user updated version of the user
     * @return "Was user successfully updated?"
     */
    public boolean updateUser(User user, String clientIp,
            PasswordStorageFile passwordFile) {

        if (!passwordFile.removeWithUserId(""+user.getID())) {
            // No such user
            logger.warning("[IP=" + clientIp + "] Attempt was made to update "
                + "nonexistent user " + user.getID() + ".");
            return false;
        }

        passwordFile.putUser(user);

        store.writeFileToDisk(passwordFile);
        //store.writeFileToStream(passwordFile, store.getPasswordsOutput());

        logger.info("[IP=" + clientIp + "] User " + user.getID()
            + " successfully updated.");

        return true;
    }

    /**
     * Returns a list of a user's stored accounts.
     * @param userId ID of user whose accounts to fetch
     * @return array of account headers corresponding to
     *      user's stored accounts (null if no stored accounts)
     */
    public Account.Header[] getAccountsForUser(int userId, String clientIp) {
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
    public Account getDetailsForAccount(int userId, int accountId, String clientIp) {
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
            String username, String password, String clientIp) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("[IP=" + clientIp + "] Attempt was made to store "
                + "a new account for nonexistent user " + userId + ".");
            return null;
        }

        int newAccountId = Integer.parseInt(userFile.getNextAccountID()); // TODO: get unique account ID (Q: Need to be random?)
        Account newAccount = new Account(newAccountId, /*userId,*/ name, username,
            password);
        userFile.putAccount(newAccount);

        store.writeFileToDisk(userFile, userId);
        //store.writeFileToStream(userFile, store.getOutputForUser(userId));

        logger.info("[IP=" + clientIp + "] New account " + newAccountId
            + " successfully stored for user " + userId + ".");

        return newAccount;
    }

    /**
     * Attempts to update an existing account.
     * Specified account must have an ID which matches an
     * existing user.
     * @param account updated version of the account
     * @return "Was account successfully updated?"
     */
    public boolean updateAccount(int userId, Account account, String clientIp) {
        //UserStorageFile userFile = store.readFileForUser(getInputForUser(account.getUserID()));
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("[IP=" + clientIp + "] Attempt was made to update "
                + "account " + account.getId() + " for nonexistent user "
                + userId + ".");
            return false;
        }

        if (!userFile.deleteAccountWithId(account.getId())) {
            // No such account existed
            logger.warning("[IP=" + clientIp + "] Attempt was made to update "
                + "nonexistent account " + account.getId() + " for user "
                + userId + ".");
            return false;
        }

        userFile.putAccount(account);

        store.writeFileToDisk(userFile, userId);
		//store.writeFileToStream(userFile, store.getOutputForUser(userId));
		////store.writeFileToStream(userFile, getOutputForUser(account.getUserID()));

        logger.info("[IP=" + clientIp + "] Account " + account.getId()
            + " for user " + userId + " successfully updated.");

        return true;
    }

    /**
     * Attempts to delete an account from a user's vault.
     * @param accountId ID of account to be deleted
     * @return "Was account successfully deleted?"
     */
    public boolean deleteAccount(int accountId, int userId, String clientIp) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            logger.warning("[IP=" + clientIp + "] Attempt was made to delete "
                + "account " + accountId + " for nonexistent user " + userId + ".");
            return false;
        }

        if (!userFile.deleteAccountWithId(accountId)) {
            // No such account
            logger.warning("[IP=" + clientIp + "] Attempt was made to delete "
                + "nonexistent account " + accountId + " for user " + userId + ".");
            return false;
        }

        store.writeFileToDisk(userFile, userId);
        //store.writeFileToStream(userFile, store.getOutputForUser(userId));

        logger.info("[IP=" + clientIp + "] Account " + accountId
            + " for user " + userId + " successfully deleted.");

        return true;
    }

    /**
     * Checks whether the specified account exists and
     * is tied to the specified user.
     * @return "Is specified account tied to specified user?"
     */
    public boolean isAccountForUser(int accountId, int userId, String clientIp) {
        UserStorageFile userFile = store.readFileForUser(store.getInputForUser(userId));
        if (userFile == null) {
            // No such user existed
            return false;
        }

        return userFile.containsAccountWithId(accountId);
    }

}

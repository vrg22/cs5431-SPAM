// Only one instance should be running at a time.
public class CentralServerController implements ServerController {
	private Logger logger;
    private ServerStorageController store;

    public CentralServerController() {
        //Set up logging
        try {
            String loggerName = CentralServerController.class.name();
            logger = SimpleLogger.getLogger(loggerName);
            logger.info("Starting up SPAM...");
        } catch (SecurityException | IOException e) {
            System.out.println("Could not start logger.");
            throw e;
        }

        // Set up storage unit
        // TODO: initialize `store`
    }

    /**
     * Checks whether the specified username/password combination
     * matches an existing user.
     * @return user ID of matching user (-1 if invalid combination)
     */
    public int login(String username, String master) {
        PasswordStorageFile passwordFile = store.getPasswordsFile();
        PasswordStorageEntry entry = passwordFile.getWithUsername(username);

        if (entry == null) {
            // No user with that username exists
            return -1;
        }

        String hashedMaster = hash(master); // TODO: implement hash
        if (!hashedMaster.equals(entry.getMaster())) {
            // Incorrect password
            return -1;
        }

        return entry.getUserId();
    }

    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String master) {
        PasswordStorageFile passwordFile = store.getPasswordsFile();

        if (passwordFile.contains("username", username)) {
            // User with that username already exists
            return null;
        }

        int newUserId = 239857; // TODO: pick random, unique user ID
        User newUser = new User(username, master, newUserId); // TODO: should password be hashed or in plaintext here?
        PasswordStorageEntry newUser = new PasswordStorageEntry(newUserId, username, master); // TODO: password should be hashed here
        passwordFile.put(newUser);

        return newUser;
    }

    /**
     * Attempts to obliterate a user's entire account.
     * @param userId ID of user to obliterate
     * @return "Was user's account successfully obliterated?"
     */
    public boolean obliterateUser(int userId) {
        PasswordStorageFile passwordFile = store.getPasswordsFile();

        boolean result = passwordFile.removeWithUserId(userId);
        if (!result) return false;

        // TODO: delete user's file too

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
        PasswordStorageFile passwordFile = store.getPasswordsFile();

        if (passwordFile.removeWithUserId(userId)) {
            passwordFile.putUser(user);
            return true;
        }

        // No such user existed
        return false;
    }

    /**
     * Returns a list of a user's stored accounts.
     * @param userId ID of user whose accounts to fetch
     * @return array of account headers corresponding to
     *      user's stored accounts (null if no stored accounts)
     */
    public Account.Header[] getAccountsForUser(int userId) {
        UserStorageFile userFile = store.getFileForUser(userId);
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
        UserStorageFile userFile = store.getFileForUser(userId);
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
        UserStorageFile userFile = store.getFileForUser(userId);
        if (userFile == null) {
            // No such user existed
            return null;
        }

        int newAccountId = 982734; // TODO: get random, unique account ID
        Account newAccount = new Account(newAccountId, userId, name, username,
            password);

        userFile.putAccount(newAccount);

        return newAccount;
    }

    /**
     * Attempts to update an existing account.
     * Specified account must have an ID which matches an
     * existing user.
     * @param account updated version of the account
     * @return "Was account successfully updated?"
     */
    public boolean updateAccount(Account account) {
        UserStorageFile userFile = store.getFileForUser(userId);
        if (userFile == null) {
            // No such user existed
            return false;
        }

        if (!deleteAccountWithId(account.getID())) {
            // No such account existed
            return false;
        }

        userFile.putAccount(account);

        return true;
    }

    /**
     * Attempts to delete an account from a user's vault.
     * @param accountId ID of account to be deleted
     * @return "Was account successfully deleted?"
     */
    public boolean deleteAccount(int accountId) {
        UserStorageFile userFile = store.getFileForUser(userId);
        if (userFile == null) {
            // No such user existed
            return false;
        }

        return userFile.deleteAccountWithId(accountId);
    }

    /**
     * Checks whether the specified account exists and
     * is tied to the specified user.
     * @return "Is specified account tied to specified user?"
     */
    public boolean isAccountForUser(int accountId, int userId) {
        UserStorageFile userFile = store.getFileForUser(userId);
        if (userFile == null) {
            // No such user existed
            return false;
        }

        return userFile.containsAccountWithId(accountId);
    }
}

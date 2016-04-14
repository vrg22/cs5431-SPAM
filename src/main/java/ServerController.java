// Provides public methods to complete user-level actions
public interface ServerController {

    /**
     * Checks whether the specified username/password combination
     * matches an existing user.
     * @return user ID of matching user (-1 if invalid combination)
     */
    // public int login(String username, String password, String clientIp,
    //         PasswordStorageFile passwordFile);

    public String getPasswordsFilename();

    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String userSalt,
            String saltedHash, String vault, String iv, String clientIp,
            PasswordStorageFile passwordFile);

    /**
     * Attempts to obliterate a user's entire account.
     * @param userId ID of user to obliterate
     * @return "Was user's account successfully obliterated?"
     */
    public boolean obliterateUser(int userId, String clientIp,
            PasswordStorageFile passwordFile);

    /**
     * Attempts to update an existing user.
     * Specified user must have an ID which matches an
     * existing user.
     * @param user updated version of the user
     * @return "Was user successfully updated?"
     */
    public boolean updateUser(User user, String clientIp,
            PasswordStorageFile passwordFile);

    /**
     * Saves an updated encrypted vault for existing user.
     */
    public void updateUserVault(int userId, String vault, String iv,
            PasswordStorageFile passwordFile);

    /**
     * Returns a list of a user's stored accounts.
     * @param userId ID of user whose accounts to fetch
     * @return array of account headers corresponding to
     *      user's stored accounts (null if no stored accounts)
     */
    // public Account.Header[] getAccountsForUser(int userId, String clientIp);

    /**
     * Returns full details for a stored accountId
     * @param accountId ID of account to fetch
     * @return full description of specified accountId (null if does not exist)
     */
    // public Account getDetailsForAccount(int userId, int accountId,
    //     String clientIp);

    /**
     * Attempts to store a new account for a user.
     * Specified user ID must match an existing user.
     * @return the account just created (null if unsuccessful)
     */
    // public Account storeNewAccountForUser(int userId, String name,
    //     String username, String password, String clientIp);

    /**
     * Attempts to update an existing account for the user with the provided ID.
     * @param account updated version of the account
     * @return "Was account successfully updated?"
     */
    // public boolean updateAccount(int userId, Account account, String clientIp);

    /**
     * Attempts to delete an account from a user's vault.
     * @param accountId ID of account to be deleted
     * @return "Was account successfully deleted?"
     */
    // public boolean deleteAccount(int accountId, int userId, String clientIp);

    /**
     * Checks whether the specified account exists and
     * is tied to the specified user.
     * @return "Is specified account tied to specified user?"
     */
    // public boolean isAccountForUser(int accountId, int userId, String clientIp);
}

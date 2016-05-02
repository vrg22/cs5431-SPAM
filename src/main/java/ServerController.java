import java.util.logging.*;

// Provides public methods to complete user-level actions
public interface ServerController {

    public String getPasswordsFilename();

    public Logger getLogger();

    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String userSalt,
            String saltedHash, String vault, String iv, String clientIp,
            PasswordStorageFile passwordFile, String encPass, String recIV,
			String recovery);

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
    public boolean updateUser1(User user, String clientIp,
            PasswordStorageFile passwordFile);

    /**
     * Saves an updated encrypted vault for existing user.
     */
    public void updateUserVault(int userId, String vault, String iv,
            PasswordStorageFile passwordFile);

	/**
	 * Updates the master password field for existing user.
	 */
	public void updateUser(int userId, String hashpass,
			PasswordStorageFile passwordFile);
}

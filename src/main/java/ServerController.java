import java.util.logging.*;

// Provides public methods to complete client-level actions
public interface ServerController {

    public String getPasswordsFilename();

    public Logger getLogger();

    //USER METHODS
    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String userSalt,
            String saltedHash, String vault, String iv, String clientIp,
            PasswordStorageFile passwordFile, String encPass, String recIV,
			String recovery, String twoFactorSecret);

    /**
     * Attempts to obliterate a user's entire account.
     * @param userId ID of user to obliterate
     * @return "Was user's account successfully obliterated?"
     */
    public boolean obliterateUser(int userId, String clientIp,
            PasswordStorageFile passwordFile);

    /**
     * Saves an updated encrypted vault for existing user.
     */
    public void updateUserVault(int userId, String vault, String iv,
            PasswordStorageFile passwordFile);

	/**
	 * Updates the master password field for existing user.
	 */
	public void updateUser(int userId, String hashpass, String encPass, String reciv,
			PasswordStorageFile passwordFile);

    //ADMIN METHODS
    /**
     * Attempts to register a new admin with the system.
     * @return the admin created (null if unsuccessful)
     */
     public Admin registerNewAdmin(String username, String adminSalt,
            String saltedHash, String adminIp,
            PasswordStorageFile passwordFile, String encPass, String reciv,
 			String recoveryHash, String twoFactorSecret);

    /**
     * Authorizes a request for admin-management privileges.
     * @return "Was attempt successful?"
     */
    public boolean authManageAdmin(String saltedHash, String clientIp);

    //TODO: Remove below two if not needed!
    /**
     * @return the system salt
     */
    public byte[] getSysSalt();

    /**
     * @return the salted hashed admin passphrase
     */
    public String getSaltedHashedAdminPhrase();

    /**
     * Attempts to obliterate an admin from the system.
     * @param adminId ID of admin to obliterate
     * @return "Was admin successfully obliterated?"
     */
    public boolean obliterateAdmin(int adminId, String clientIp,
            PasswordStorageFile passwordFile);
    //TODO: Need to add master-password updating methods?
    
    /**
     * @return the readable contents of all logs on disk at server as well as their original names
     */
	public String[][] getLogInfo();

    /**
     * @return "Was a log with specified name successfully deleted?"
     */
	public boolean deleteLog(String logName);
}

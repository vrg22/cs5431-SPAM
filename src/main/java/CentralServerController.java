import java.util.logging.*;

import org.w3c.dom.Document;

import java.io.*;

// Provides public methods to complete user-level actions
public class CentralServerController implements ServerController {
	public Logger logger;
    private StorageController store;
	private CryptoServiceProvider crypto;
    private String passwordFilename;

    public CentralServerController(String logLocation, String passwordFilename)
            throws SecurityException, IOException {
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
        this.passwordFilename = passwordFilename;
        store = new XMLStorageController(passwordFilename);
        if (!(new File(store.getPasswordsFilename()).exists())) {
            store.createPasswordsFileOnStream(store.getPasswordsOutput());
        }

		// Set up the crypto module
		crypto = new CryptoServiceProvider();
    }

    public String getPasswordsFilename() {
        return passwordFilename;
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
            String saltedHash, String vault, String iv, String clientIp,
            PasswordStorageFile passwordFile) {

        if (passwordFile.contains("username", username)) {
            // User with that username already exists
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "register a new user with existing username " + username + ".");
            return null;
        }

        // Add user to main password file
        int newUserId = Integer.parseInt(passwordFile.getNextID());  // TODO: pick unique user ID (Q: Need to be random?)

        User newUser = new User(username, CryptoServiceProvider.b64decode(userSalt),
            saltedHash, newUserId, CryptoServiceProvider.b64decode(iv));

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

    public void updateUserVault(int userId, String vault, String iv,
            PasswordStorageFile passwordFile) {
        store.writeEncryptedUserFileToDisk(userId, vault);

        PasswordStorageEntry entry = passwordFile.getWithUserId(""+userId);
        entry.setIV(iv);
    }
}

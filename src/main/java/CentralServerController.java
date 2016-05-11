import java.util.*;
import java.util.logging.*;
import org.w3c.dom.Document;
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
//import org.apache.commons.io.FilenameUtils;

// Provides public methods to complete user-level actions
public class CentralServerController implements ServerController {
	private Logger logger;
	private FileHandler fh;
    private String logLocation;
    private File currentLogFile;
    private StorageController store;
    private String passwordFilename;
    private String adminPassword;
    private byte[] systemSalt;

    public CentralServerController(String logLocation, String passwordFilename, String adminPassword, byte[] sysSalt)
            throws SecurityException, IOException {
        //Set up logging
        try {
            String loggerName = CentralServerController.class.getName();
            logger = Logger.getLogger(loggerName);

            fh = new FileHandler(logLocation, true);
            this.logLocation = logLocation;
            this.currentLogFile = new File(logLocation);
            logger.addHandler(fh);

            SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);

            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            try {
                Files.setPosixFilePermissions(Paths.get(logLocation), perms);
            } catch (IOException e) {}

            logger.info("Starting up SPAM...");
        } catch (SecurityException | IOException e) {
            System.out.println("Could not start logger.");
            throw e;
        }

        // Set up storage
        // TODO: Protect createPasswordsFileOnStream by synchronization OR ensure this method only ever gets called once
        this.passwordFilename = passwordFilename;
        store = new XMLStorageController(passwordFilename);
        if (!(new File(store.getPasswordsFilename()).exists())) {
            store.createPasswordsFileOnStream(store.getPasswordsOutput());
        }

        // Store adminphrase and salt
        this.adminPassword = adminPassword;
        this.systemSalt = sysSalt.clone();
    }

    public String getPasswordsFilename() {
        return passwordFilename;
    }

    public Logger getLogger() {
        if (currentLogFile.exists()) {
            long bytes = currentLogFile.length();
            // Log file size exceed 100 MB
            if (bytes >= (1024 * 1024 * 100)) {
                try {
                    logger.removeHandler(fh);
                    fh.close();
                    Main.encryptLogFile(currentLogFile);
                    logLocation = new SimpleDateFormat("yyyyMMddhhmm'.log'").format(new Date());
                    System.out.println("new Log file name is " + logLocation);
                    Main.setLogLocation(logLocation);
                    fh = new FileHandler(logLocation, true);
                    logger.addHandler(fh);
                    currentLogFile = new File(logLocation);
                } catch (IOException e) {
                    System.out.println("IOException when resetting log file");
                }
            }
        }
        return logger;
    }

    //USER METHODS
    /**
     * Attempts to register a new user with the system.
     * @return the user created (null if unsuccessful)
     */
    public User registerNewUser(String username, String userSalt,
            String saltedHash, String vault, String iv, String clientIp,
            PasswordStorageFile passwordFile, String encPass, String reciv,
			String recoveryHash, String twoFactorSecret) {

        //if (passwordFile.contains("username", username)) {
    	if (passwordFile.containsWithType("user", "username", username)) {
            // User with that username already exists
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "register a new user with existing username " + username + ".");
            return null;
        }

        // Add user to main password file
        int newUserId = Integer.parseInt(passwordFile.getNextUserID());

        User newUser = new User(username, CryptoServiceProvider.b64decode(userSalt),
            saltedHash, newUserId, CryptoServiceProvider.b64decode(iv), encPass,
			CryptoServiceProvider.b64decode(reciv), recoveryHash, twoFactorSecret);

        PasswordStorageEntry newUserEntry = new PasswordStorageEntry(newUser);
        passwordFile.put(newUserEntry);

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

        // Delete user from passwords file
        if (!passwordFile.removeWithId("user", ""+userId)) {
            // No such user
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "obliterate nonexistent user " + userId + ".");
            return false;
        }
        store.writeFileToDisk(passwordFile);

        // Delete user's vault file
        // TODO: Delete lock for that file here as well? (But need to ensure no conflicts first)
        try {
            Files.delete(FileSystems.getDefault().getPath(
                store.getFilenameForUser(userId)));
        } catch (NoSuchFileException x) {
            System.err.println("User vault file not found.");
        } catch (DirectoryNotEmptyException x) {
            System.err.println("User vault file not found");
        } catch (IOException x) {
            // File permission problems are caught here.
            System.err.println(x);
        }

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
    public boolean updateUser1(User user, String clientIp,
            PasswordStorageFile passwordFile) {

        if (!passwordFile.removeWithId("user", ""+user.getId())) {
            // No such user
            logger.warning("[IP=" + clientIp + "] Attempt was made to update "
                + "nonexistent user " + user.getId() + ".");
            return false;
        }

        passwordFile.putUser(user);

        store.writeFileToDisk(passwordFile);

        logger.info("[IP=" + clientIp + "] User " + user.getId()
            + " successfully updated.");

        return true;
    }

    public void updateUserVault(int userId, String vault, String iv,
            PasswordStorageFile passwordFile) {
        store.writeEncryptedUserFileToDisk(userId, vault);

        PasswordStorageEntry entry = passwordFile.getWithId("user", ""+userId);
        entry.setIV(iv);

        store.writeFileToDisk(passwordFile);
    }

	public void updateUser(int userId, String hashpass, String encPass,
			String reciv, PasswordStorageFile passwordFile) {
        PasswordStorageEntry entry = passwordFile.getWithId("user", ""+userId);
        entry.setMaster(hashpass);
		entry.setEncPass(encPass);
		entry.setRecIV(reciv);

		System.out.println("updateUser wrote to disk: " +hashpass);
        store.writeFileToDisk(passwordFile);
	}


    //ADMIN-SPECIFIC METHODS
    /**
     * Authorizes a request for admin-management privileges.
     * @return "Was attempt successful?"
     */
    public boolean authManageAdmin(String saltedHash, String clientIp) {
    	// Generate salted hashed admin passphrase
    	String saltedAdminPassphrase = getSaltedHashedAdminPhrase();
    	
    	//If salted hash matches, return list of admins
        if (!saltedHash.equals(saltedAdminPassphrase)) {
            logger.warning("[IP=" + clientIp + "] "
                + "Incorrect password while attempting to gain "
                + "admin management access.");
            return false;
        }

        return true;
    }

    //TODO: Remove the below two!
    /**
     * @return the system salt
     */
    public byte[] getSysSalt() {
    	return systemSalt.clone();
    }

    /**
     * @return the salted hashed admin passphrase
     */
    public String getSaltedHashedAdminPhrase() {
    	return CryptoServiceProvider.genSaltedHash(adminPassword, systemSalt);
    }

    /**
     * Attempts to register a new admin with the system.
     * @return the admin created (null if unsuccessful)
     */
    public Admin registerNewAdmin(String username, String adminSalt,
            String saltedHash, String adminIp,
            PasswordStorageFile passwordFile, String encPass, String reciv,
			String recoveryHash, String twoFactorSecret) {
    	if (passwordFile.containsWithType("admin", "username", username)) {
            // Admin with that username already exists
            logger.warning("[IP=" + adminIp + "] Attempt was made to "
                + "register a new admin with existing username " + username + ".");
            return null;
        }
    	
        // Add admin to main password file
        int newAdminId = Integer.parseInt(passwordFile.getNextAdminID());
        
        Admin newAdmin = new Admin(username, CryptoServiceProvider.b64decode(adminSalt),
            saltedHash, newAdminId, encPass, CryptoServiceProvider.b64decode(reciv),
            recoveryHash, twoFactorSecret);

        passwordFile.putAdmin(newAdmin);

        store.writeFileToDisk(passwordFile);

        logger.info("[IP=" + adminIp + "] New admin " + newAdminId
            + " successfully registered.");
        return newAdmin;
    }

    /**
     * Attempts to obliterate an admin from the system.
     * @param adminId ID of admin to obliterate
     * @return "Was admin successfully obliterated?"
     */
    public boolean obliterateAdmin(int adminId, String clientIp,
            PasswordStorageFile passwordFile) {

        // Delete user from passwords file
        if (!passwordFile.removeWithId("admin", ""+adminId)) {
            // No such admin
            logger.warning("[IP=" + clientIp + "] Attempt was made to "
                + "obliterate nonexistent admin " + adminId + ".");
            return false;
        }
        store.writeFileToDisk(passwordFile);

        logger.info("[IP=" + clientIp + "] Admin " + adminId
            + " successfully obliterated.");

        return true;
    }
    
    /**
     * @return the readable contents of all logs on disk at server
     */
	public String[][] getLogInfo() { //TODO: Log for failures?
		// Get all .enclog files
		Path currentRelativePath = Paths.get("");
		String dir = currentRelativePath.toAbsolutePath().toString();
		File[] enclogs = getEncLogs(dir);
		String[][] logInfo = new String[2][enclogs.length];
		
		// Populate logs with the decrypted log contents
		for (int i=0; i<enclogs.length; i++) {
			//Read file into string
			File f = enclogs[i];
			String encrypted;
			try {
				encrypted = readFile(f.getPath(), Charset.defaultCharset());

				String plainName = f.getName();
				String fileNameWithOutExt = plainName.substring(0, (plainName.length()-(".enclog".length())));
				//FilenameUtils.removeExtension(f.getName());
				byte[] iv = CryptoServiceProvider.b64decode(Main.filenameToB64(fileNameWithOutExt));
				logInfo[1][i] = plainName;
				logInfo[0][i] = CryptoServiceProvider.decrypt(encrypted, adminPassword, getSysSalt(), iv);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return logInfo;
	}
	
    /**
     * Attempts to delete the encrypted logfile with the specified name.
     * 
     * @return "Was a log with specified name successfully deleted?"
     */
	public boolean deleteLog(String logName) {		//TODO: Include IP in log entry?
		try {
			// Basic Sanitization: ends in .enclog
			if (!(logName.length() > ".enclog".length()) || !logName.endsWith(".enclog")) {
	            logger.warning("Attempt was made to "
	            		+ "obliterate nonexistent encrypted log file \"" + logName + "\".");
	            return false;
			}
			return Files.deleteIfExists(Paths.get(logName));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/* HELPERS */
	//From: http://stackoverflow.com/questions/1384947/java-find-txt-files-in-specified-folder
	private File[] getEncLogs(String dirName) {
    	File dir = new File(dirName);

    	return dir.listFiles(new FilenameFilter() { 
    	         public boolean accept(File dir, String filename)
    	              { return filename.endsWith(".enclog"); }
    	} );
    }
	
	//From: http://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file
	private String readFile(String path, Charset encoding) throws IOException {
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
}

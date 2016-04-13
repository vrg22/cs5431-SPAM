import java.io.*;

// Handles parsing storage files
public interface StorageController {

    /**
     * Read passwords file from input stream, return as a StorageFile
     */
    public PasswordStorageFile readPasswordsFile(FileInputStream in);

    /**
     * Read user file from input stream, return as a StorageFile
     */
    public UserStorageFile readFileForUser(FileInputStream in);




    /**
     * Create a new passwords file, write to designated output stream
     */
    public void writeFileToDisk(PasswordStorageFile file);

    /**
     * Create a new passwords file, write to designated output stream
     */
    public void writeFileToDisk(UserStorageFile file, int userId);
    /**
     * Create a new passwords file, write to designated output stream
     */
    public void createPasswordsFileOnStream();

    /**
     * Create a new user file, write to designated output stream
     */
    public void createFileForUserOnStream(int userId);



    //THE BELOW 4 ARE UNUSED
    /**
     * Create a new passwords file, write to output stream
     */
    public void createPasswordsFileOnStream(FileOutputStream out);

    /**
     * Create a new user file, write to output stream
     */
    public void createFileForUserOnStream(int userId, FileOutputStream out);
    /**
     * Convert a PasswordStorageFile into file-writable form,
     * and write to output stream
     */
    public void writeFileToStream(PasswordStorageFile file, FileOutputStream out);

    /**
     * Convert a UserStorageFile into file-writable form,
     * and write to output stream
     */
    public void writeFileToStream(UserStorageFile file, FileOutputStream out);

    /**
     * Return file extension that this controller uses to store files (e.g., ".xml")
     */
    public String getExtension();

    /**
     * Return filename that this controller uses for main password info.
     */
    public String getPasswordsFilename();

    /**
     * Return file extension that this controller uses for a particular user.
     */
    public String getFilenameForUser(int userId);

    /**
     * Return file input stream for the main password file.
     */
    public FileInputStream getPasswordsInput();

    /**
     * Return file input stream for the indicated user vault file.
     */
    public FileInputStream getInputForUser(int userId);

    /**
     * Return file output stream for the main password file.
     */
    public FileOutputStream getPasswordsOutput();

    /**
     * Return file output stream for the indicated user vault file.
     */
    public FileOutputStream getOutputForUser(int userId);

    public void writeEncryptedUserFileToDisk(int userId, String contents);

    public void setupUserXML(StringBuilder sb, int ID);
}

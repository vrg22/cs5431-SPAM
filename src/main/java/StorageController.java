import java.io.*;

// Handles parsing storage files
public interface StorageController {
    /**
     * Create a new passwords file, write to output stream
     */
    public void createPasswordsFileOnStream(OutputStream out);

    /**
     * Create a new user file, write to output stream
     */
    public void createFileForUserOnStream(int userId, OutputStream out);

    /**
     * Read passwords file from input stream, return as a StorageFile
     */
    public PasswordStorageFile readPasswordsFile(InputStream in);

    /**
     * Read user file from input stream, return as a StorageFile
     */
    public UserStorageFile readFileForUser(InputStream in);

    /**
     * Convert a PasswordStorageFile into file-writable form,
     * and write to output stream
     */
    public void writeFileToStream(PasswordStorageFile file, OutputStream out);

    /**
     * Convert a UserStorageFile into file-writable form,
     * and write to output stream
     */
    public void writeFileToStream(UserStorageFile file, OutputStream out);

    /**
     * Return file extension that this controller uses to store files (e.g., ".xml")
     */
    public String getExtension();
}

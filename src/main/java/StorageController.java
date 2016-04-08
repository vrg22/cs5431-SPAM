package main.java;

import java.io.FileInputStream;
import java.io.FileOutputStream;

// Handles parsing storage files
public interface StorageController {
    /**
     * Create a new passwords file, write to output stream
     */
    public void createPasswordsFileOnStream(FileOutputStream out);

    /**
     * Create a new user file, write to output stream
     */
    public void createFileForUserOnStream(int userId, FileOutputStream out);

    /**
     * Read passwords file from input stream, return as a StorageFile
     */
    public PasswordStorageFile readPasswordsFile(FileInputStream in);

    /**
     * Read user file from input stream, return as a StorageFile
     */
    public UserStorageFile readFileForUser(FileInputStream in);

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
}

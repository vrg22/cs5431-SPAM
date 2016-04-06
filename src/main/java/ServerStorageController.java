public interface ServerStorageController {
    public void createPasswordsFile();
    public void createFileForUser(int userId);
    public PasswordStorageFile getPasswordsFile();
    public UserStorageFile getFileForUser(int userId);
}

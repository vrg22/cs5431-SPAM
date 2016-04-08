public class PasswordStorageFile extends StorageFile {
    public PasswordStorageEntry getWithUserId(String userId) {
        return (PasswordStorageEntry)get("userid", userId);
    }

    public PasswordStorageEntry getWithUsername(String username) {
        return (PasswordStorageEntry)get("username", username);
    }

    public PasswordStorageEntry getWithMaster(String master) {
        return (PasswordStorageEntry)get("master", master);
    }

    public void putUser(User user) {
        put(new PasswordStorageEntry(user));
    }

    public boolean removeWithUserId(String userId) {
        return remove("userid", userId);
    }

    public boolean removeWithUsername(String username) {
        return remove("username", username);
    }

    public boolean containsUserId(String userId) {
        return contains("userid", userId);
    }

    public boolean containsUsername(String username) {
        return contains("username", username);
    }
}

public abstract class StorageEntry {
    private Map<String, String> values;

    public StorageEntry() {
        values = new HashMap<String, String>();
    }

    protected String get(String key) {
        return values.get(key);
    }

    protected void put(String key, String value) {
        values.put(key, value);
    }

    // Keys: user ID, username, hashed-salted master password
    public static class PasswordStorageEntry implements StorageEntry {
        // Note: master password here should already be hashed
        public PasswordStorageEntry(User user) {
            super();

            put("userid", user.getID());
            put("username", user.getUsername());
            put("master", user.getPassword());
        }

        public int getUserId() {
            return get("userid");
        }

        public String getUsername() {
            return get("username");
        }

        public String getMaster() {
            return get("master");
        }
    }

    // Keys: account ID, user ID, name, username, plaintext password
    public static class UserStorageEntry implements StorageEntry {
        public UserStorageEntry(int accountId, int userId, String name, String username,
                String password) {
            super();

            put("accountid", accountId);
            put("userid", userId);
            put("name", name);
            put("username", username);
            put("password", password);
        }

        public Account getAccount() {
            int accountId = Integer.parseInt(get("accountid"));
            int userId = Integer.parseInt(get("userid"));
            String name = get("name");
            String username = get("username");
            String password = get("password");
            return new Account(accountId, userId, name, username, password);
        }

        public Account.Header getHeader() {
            int accountId = Integer.parseInt(get("accountid"));
            int userId = Integer.parseInt(get("userid"));
            String name = get("name");
            return new Account.Header(accountId, userId, name);
        }

        public int getAccountId() {
            return Integer.parseInt(get("accountid"));
        }
    }
}
public abstract class StorageFile {
    private List<StorageEntry> entries;

    public StorageFile() {
        entries = new ArrayList<StorageEntry>();
    }

    protected StorageEntry get(String key, String value) {
        for (StorageEntry entry : entries) {
            if (entry.get(key).equals(value)) {
                return entry;
            }
        }

        return null;
    }

    // Precondition: `entry` does not conflict with any existing entries
    protected void put(StorageEntry entry) {
        entries.add(entry);
    }

    // @return true if element did exist and was removed, false otherwise
    protected boolean remove(String key, String value) {
        Iterator<StorageEntry> iter = entries.iterator();
        while (iter.hasNext()) {
           StorageEntry entry = iter.next();

           if (entry.get(key).equals(value)) {
               iter.remove();
               return true;
           }
        }

        // No such element existed
        return false;
    }

    protected boolean contains(String key, String value) {
        return get(key, value) == null;
    }

    public static class PasswordStorageFile extends StorageFile {
        public PasswordStorageEntry getWithUserId(String userId) {
            return (PasswordStorageFile)get("userid");
        }

        public PasswordStorageEntry getWithUsername(String username) {
            return (PasswordStorageFile)get("username");
        }

        public PasswordStorageEntry getWithMaster(String master) {
            return (PasswordStorageFile)get("master");
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
            return contains("userid");
        }

        public boolean containsUsername(String username) {
            return contains("username");
        }
    }

    public static class UserStorageFile extends StorageFile {
        public Account.Header[] getAccountHeaders() {
            Account.Header[] headers = new Account.Header[entries.size()];

            for (int i = 0; i < headers.length; i++) {
                UserStorageEntry entry = entries.get(i);
                headers[i] = entry.getHeader();
            }

            return headers;
        }

        public Account getAccountWithId(int accountId) {
            for (UserStorageEntry entry : entries) {
                if (entry.getAccountId() == accountId) {
                    return entry.getAccount();
                }
            }

            // No such account existed
            return null;
        }

        public void putAccount(Account account) {
            UserStorageEntry newEntry = new UserStorageEntry(account.getID(),
                account.getUserID(), account.getName(), account.getUsername(),
                account.getPassword());

            put(newEntry);
        }

        public boolean deleteAccountWithId(int accountId) {
            return remove("accountid", accountId);
        }

        public boolean containsAccountWithId(int accountId) {
            return contains("accountid", accountId);
        }
    }
}

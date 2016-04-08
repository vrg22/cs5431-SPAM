public class UserStorageFile extends StorageFile {
    public Account.Header[] getAccountHeaders() {
        Account.Header[] headers = new Account.Header[entries.size()];

        for (int i = 0; i < headers.length; i++) {
            UserStorageEntry entry = (UserStorageEntry)entries.get(i);
            headers[i] = entry.getHeader();
        }

        return headers;
    }

    public Account getAccountWithId(int accountId) {
        for (StorageEntry se : entries) {
            UserStorageEntry entry = (UserStorageEntry)se;
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
        return remove("accountid", ""+accountId);
    }

    public boolean containsAccountWithId(int accountId) {
        return contains("accountid", ""+accountId);
    }
}

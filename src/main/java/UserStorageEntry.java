// Keys: account ID, name, username, plaintext password
public class UserStorageEntry extends StorageEntry {
    public UserStorageEntry(int accountId, String name, String username,
            String password) {
        super();

        put("accountid", ""+accountId);
        put("name", name);
        put("username", username);
        put("password", password);
    }

    public Account getAccount() {
        int accountId = Integer.parseInt(get("accountid"));
        String name = get("name");
        String username = get("username");
        String password = get("password");
        return new Account(accountId, /*userId,*/ name, username, password);
    }

    public Account.Header getHeader() {
        int accountId = Integer.parseInt(get("accountid"));
        String name = get("name");
        return new Account.Header(accountId, name);
    }

    public int getAccountId() {
        return Integer.parseInt(get("accountid"));
    }
}

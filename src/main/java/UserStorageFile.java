import java.util.ArrayList;

public class UserStorageFile extends StorageFile {
	
	//Metadata for a particular user's vault file
	int userID;
	int nextAccountID;
	int numRecords;
	
	//Constructor
	public UserStorageFile(int uID) {
		super();
		
		this.userID = uID;
		this.nextAccountID = 0;
		this.numRecords = 0;
	}
		
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
            /*account.getUserID(),*/ account.getName(), account.getUsername(),
            account.getPassword());
        
        nextAccountID++; //TODO: Update to next AVAILABLE ID
        numRecords++;
        
        put(newEntry);
    }

    public boolean deleteAccountWithId(int accountId) {
    	numRecords--;
    	
        return remove("accountid", ""+accountId);
    }

    public boolean containsAccountWithId(int accountId) {
        return contains("accountid", ""+accountId);
    }
    
    
    //Retrieving metadata
    public String getUserID() {
    	return Integer.toString(userID);
    }
    
    public String getNextAccountID() {
    	//TODO: Can decide to update this value here, and then 
    	return Integer.toString(nextAccountID);
    }
    
    // Setting metadata (for use ONLY when converting DOM to UserStorageFile)
    public void setRecords(ArrayList<Account> accts) {
    	//Assumption: file on disk is valid, so we can simply set these variables
    	for (Account a : accts) {
    		putAccount(a);
    	}
    }
}

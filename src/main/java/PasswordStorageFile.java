import java.util.ArrayList;

public class PasswordStorageFile extends StorageFile {
	
	//Metadata for the main password file
	int nextID;
	int numUsers;
	
	//Constructor
	public PasswordStorageFile() {
		super();
		
		int nextID = 0;
		int numUsers = 0;
	}
	
    public PasswordStorageEntry getWithUserId(String userId) {
        return (PasswordStorageEntry)get("userid", userId);
    }

    public PasswordStorageEntry getWithUsername(String username) {
        return (PasswordStorageEntry)get("username", username);
    }

    public PasswordStorageEntry getWithMaster(String master) { //TODO: This may not be viable, because multiple users may have the same password
        return (PasswordStorageEntry)get("master", master);
    }

    public void putUser(User user) {
    	// TODO: VERIFY that the user already used the nextID field to set user's ID
    	// TODO: Find the next 'nextID' value that doesn't correspond but ONLY if it doesn't duplicate
    	// TODO: Check max users
    	if (numUsers < User.MAX_USERS) {
    		numUsers++;
            put(new PasswordStorageEntry(user));
            // TODO: Advance nextID to next valid number, if numUsers < MAX_USERS -> Or could build this into "getNextID"
    	}
    	else {
    		/* RAISE SOME EXCEPTION */
    	}
    }

    public boolean removeWithUserId(String userId) {
    	numUsers--;
        return remove("userid", userId);
    }

    public boolean removeWithUsername(String username) {
    	numUsers--;
        return remove("username", username);
    }

    public boolean containsUserId(String userId) {
        return contains("userid", userId);
    }

    public boolean containsUsername(String username) {
        return contains("username", username);
    }
    
    
    // Retrieving metadata
    public String readNextID() {
    	return Integer.toString(nextID);
    }
    
    public String getNextID() {
    	//TODO: Could add logic to auto-find the next valid ID from users!
    	return Integer.toString(nextID);
    }
    
    public String getNumUsers() {
    	return Integer.toString(numUsers);
    }
    
    public static String getPasswordsFilename() {
        return "users";
    }
    
    // Setting metadata (for use ONLY when converting DOM to PasswordStorageFile)
//    public void setMetadata(int nextId, int nUsers) {
//    	this.nextID = nextId;
//    	this.numUsers = nUsers;
//    }
    
    public void setUsers(ArrayList<User> users) {
    	//Assumption: file on disk is valid, so we can simply set these variables
    	for (User u : users) {
    		putUser(u);
    	}
    }    
    
}

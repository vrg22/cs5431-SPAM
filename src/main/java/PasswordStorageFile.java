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

    public void putUser(User user) {
    	// TODO: VERIFY that the user already used the nextID field to set user's ID
    	// TODO: Find the next 'nextID' value that doesn't correspond but ONLY if it doesn't duplicate
    	if (numUsers < User.MAX_USERS) {
    		numUsers++;
            put(new PasswordStorageEntry(user));
    	}
    	/*
    	else {
    		// TODO: RAISE SOME EXCEPTION
    	}
    	*/
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

    //Obtains AN unused ID for a user, assuming User.MAX_USERS is not reached.
    public String getNextID() {
    	while(containsUserId(Integer.toString(nextID)) && numUsers < User.MAX_USERS) {
    		nextID++;
    	}
    	return Integer.toString(nextID);
    }

    public String getNumUsers() {
    	return Integer.toString(numUsers);
    }

    // For use ONLY when converting DOM to PasswordStorageFile
    public void setUsers(ArrayList<User> users) {
    	//Assumption: file on disk is valid, so we can simply set these variables
    	for (User u : users) {
    		putUser(u);
    	}
    }

}

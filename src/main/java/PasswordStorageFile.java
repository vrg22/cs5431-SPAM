import java.util.ArrayList;

public class PasswordStorageFile extends StorageFile {

	//Metadata for the main password file
	int nextUserID;
	int nextAdminID;
	int numUsers;
	int numAdmins;

	//Constructor
	public PasswordStorageFile() {
		super();

		nextUserID = 0;
		nextAdminID = 0;
		numUsers = 0;
		numAdmins = 0;
	}

    public PasswordStorageEntry getWithId(String type, String userId) {
       	return (PasswordStorageEntry)getWithType(type, "id", userId);
    }

    public PasswordStorageEntry getWithUsername(String type, String username) {
        return (PasswordStorageEntry)getWithType(type, "username", username);
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

    public void putAdmin(Admin admin) {
    	// TODO: Check all same concerns as for putUser
    	if (numAdmins < Admin.MAX_ADMINS) {
    		numAdmins++;
            put(new PasswordStorageEntry(admin));
    	}
    	/*
    	else {
    		// TODO: RAISE SOME EXCEPTION
    	}
    	*/
    }

    public void putClient(Client client) {
    	if (client instanceof User){
    		putUser((User) client);
    	}
    	else if (client instanceof Admin){
    		putAdmin((Admin) client);
    	}
    	/*
    	else {
    		// TODO: RAISE SOME EXCEPTION
    	}
    	*/
    }

    //Decrements either user or admin count, based on string
    private void decrementByType(String type) {
    	if (type.equals("user")) {
    		numUsers--;
    	}
    	else if (type.equals("admin")) {
    		numAdmins--;
    	}
    }

    public boolean removeWithId(String type, String id) {
    	decrementByType(type);
    	return removeWithType(type, "id", id);
    }

    public boolean removeWithUsername(String type, String username) {
    	decrementByType(type);
        return removeWithType(type, "username", username);
    }

    public boolean containsWithId(String type, String id) {
        return containsWithType(type, "id", id);
    }

    public boolean containsUsername(String type, String username) {
        return containsWithType(type, "username", username);
    }


    // Retrieving metadata

    //Obtains AN unused ID for a user, assuming User.MAX_USERS is not reached.
    public String getNextUserID() {
    	while(containsWithId("user", Integer.toString(nextUserID)) && numUsers < User.MAX_USERS) {
    		nextUserID++;
    	}
    	return Integer.toString(nextUserID);
    }

    //Obtains AN unused ID for an admin, assuming Admin.MAX_ADMINS is not reached.
    public String getNextAdminID() {
    	while(containsWithId("admin", Integer.toString(nextAdminID)) && numAdmins < Admin.MAX_ADMINS) {
    		nextAdminID++;
    	}
    	return Integer.toString(nextAdminID);
    }

    public String getNumUsers() {
    	return Integer.toString(numUsers);
    }

    public String getNumAdmins() {
    	return Integer.toString(numAdmins);
    }

    // For use ONLY when converting DOM to PasswordStorageFile
    public void setUsers(ArrayList<User> users) {
    	//Assumption: file on disk is valid, so we can simply set these variables
    	for (User u : users) {
    		putUser(u);
    	}
    }

    // For use ONLY when converting DOM to PasswordStorageFile
    public void setAdmins(ArrayList<Admin> admins) {
    	//Assumption: file on disk is valid, so we can simply set these variables
    	for (Admin a : admins) {
    		putAdmin(a);
    	}
    }

    /**
     * Extracts admins as file prepared for Admin management.
     * @return the produced file abstraction
     */
    public AdminManagementFile getAdminFile() {
    	AdminManagementFile amFile = new AdminManagementFile();
    	for (StorageEntry tmpEntry : entries) {
            PasswordStorageEntry entry = (PasswordStorageEntry)tmpEntry;
            if (entry.getType().equals("admin")) {
                amFile.putAdmin(new Admin(entry.getUsername(),
                    entry.getSalt(), entry.getMaster(), entry.getId(),
                    entry.getEncPass(), entry.getRecIV(),
                    entry.getRecovery(), entry.getTwoFactorSecret()));
            }
        }

    	return amFile;
    }

}

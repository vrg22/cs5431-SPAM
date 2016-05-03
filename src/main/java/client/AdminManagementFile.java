import java.util.ArrayList;

//Representation of all existing administrator accounts
public class AdminManagementFile extends StorageFile {
	
	//Metadata for managing admins
	int nextAdminID;
	int numAdmins;
	
	//Constructor
	public AdminManagementFile() {
		super();
		
		nextAdminID = 0;
		numAdmins = 0;
	}

	// Methods to add or delete Admins within the entries of this file
	// Q: Do this by "updating" existing admins, or by removing and then adding admins?
	
	public AdminEntry getAdmin(String username) {
		return (AdminEntry)get("username", username);
	}
    
    public boolean containsWithId(String type, String id) {
        return containsWithType(type, "id", id);
    }
    
    //Obtains AN unused ID for an admin, assuming Admin.MAX_ADMINS is not reached.
    public String getNextAdminID() {
    	while(containsWithId("admin", Integer.toString(nextAdminID)) && numAdmins < Admin.MAX_ADMINS) {
    		nextAdminID++;
    	}
    	return Integer.toString(nextAdminID);
    }
    
	public void putAdmin(Admin admin) {
    	if (numAdmins < Admin.MAX_ADMINS) {
    		numAdmins++;
            put(new AdminEntry(admin));
    	}
    	/*
    	else {
    		// TODO: RAISE SOME EXCEPTION
    	}
    	*/
    }
	
	public boolean deleteAdmin(String username) {
		boolean result = remove("username", username);
		if (result) {
			numAdmins--;
		}
		return result;
	}
	
    public Admin.Header[] getAdmins() {
        Admin.Header[] headers = new Admin.Header[entries.size()];

        for (int i = 0; i < headers.length; i++) {
            AdminEntry entry = (AdminEntry)entries.get(i);
            headers[i] = entry.getHeader();
        }

        return headers;
    }
}

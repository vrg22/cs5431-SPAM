import java.util.List;
import java.util.ArrayList;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

//Representation of all existing administrator accounts
public class AdminManagementFile extends StorageFile {
	
	//Metadata for managing admins
	private int nextAdminID;
	private int numAdmins;
	
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
    
    // Gets the current num admins
    public int getNumAdmins() {
    	return numAdmins;
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
        	StorageEntry entry_test = entries.get(i);
        	if (! (entry_test instanceof AdminEntry) ) {
        		System.out.println(entry_test.toString());
        	}
        	AdminEntry entry = (AdminEntry) entry_test;
            //AdminEntry entry = (AdminEntry)entries.get(i);
            headers[i] = entry.getHeader();
        }

        return headers;
    }
    
    // Return Java array of admins in "entries"
    public AdminEntry[] getEntries() {
    //public ArrayList<AdminEntry> getEntries() {
        //StorageEntry[] admins = new StorageEntry[entries.size()];

        AdminEntry[] admins = new AdminEntry[entries.size()];
        return entries.toArray(admins);
    	
    	/*
    	ArrayList<AdminEntry> admins = new ArrayList<AdminEntry>();
    	for (StorageEntry e : entries) {
    		admins.add((AdminEntry) e);
    	}
    	
    	return admins;

    	*/
    	
    }
    
    //Serialization of AdminManagementFile
    /*
    public static class AdminManagementFileSerializer implements JsonSerializer<AdminManagementFile> {
        public JsonElement serialize(final AdminManagementFile amfile, final Type type, final JsonSerializationContext context) {
            JsonObject result = new JsonObject();
            result.add("nxtid", new JsonPrimitive(amfile.getNextAdminID()));
            result.add("numadmins", new JsonPrimitive(amfile.getNumAdmins())); //Should make get string?
            result.add("admins", new JsonPrimitive(amfile.getEntries()));
            
            return result;
        }
    }
    */
    
}

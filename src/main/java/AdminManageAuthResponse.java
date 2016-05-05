//import java.util.List;
//import java.util.ArrayList;

public class AdminManageAuthResponse {
//public class AdminManageAuthResponse<AdminEntry> {

    //private AdminManagementFile amFile;
	
    //private int nextAdminID;
	//private int numAdmins;

	private AdminEntry[] entries;
	//private List<StorageEntry> entries;
	//private ArrayList<AdminEntry> entries;
	//private List<? extends StorageEntry> entries;
	
    public AdminManageAuthResponse(AdminManagementFile amFile) {
        //nextAdminID = Integer.parseInt(amFile.getNextAdminID());
        //numAdmins = amFile.getNumAdmins();
        entries = amFile.getEntries();
    }

    public AdminManagementFile getAMFile() {    	
    	AdminManagementFile amFile = new AdminManagementFile();
    	
    	for (AdminEntry a : entries) {
    		amFile.putAdmin(a.toAdmin());
    	}
    	
    	return amFile;
    }
    
}

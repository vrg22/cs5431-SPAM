
public class AdminManageAuthResponse {

    private AdminManagementFile amFile;

    public AdminManageAuthResponse() { //Some GSON error??? No??
    	
    }
    
    public AdminManageAuthResponse(AdminManagementFile amFile) {
        this.amFile = amFile;
    }

    public AdminManagementFile getAMFile() {
        return amFile;
    }
}

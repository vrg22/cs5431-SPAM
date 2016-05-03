
// Representation of single admin within AdminManagementFile
public class AdminEntry extends StorageEntry {

	public AdminEntry(Admin admin) {
        super();
        
        put("id", ""+admin.getId());
        put("username", admin.getUsername());
        put("master", admin.getMaster());
        //put("salt", CryptoServiceProvider.b64encode(client.getSalt()));

		//put("iv", CryptoServiceProvider.b64encode(admin.getIV()));
    }

	public String getId() {
        return get("id");
    }
	
    public String getUsername() {
        return get("username");
    }

    public String getMaster() {
        return get("master");
    }

	public Admin.Header getHeader() {
		return new Admin.Header(Integer.parseInt(get("id")), get("username"));
	}
    
}

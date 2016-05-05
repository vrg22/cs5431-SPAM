
// Representation of single admin within AdminManagementFile
public class AdminEntry extends StorageEntry {

	public AdminEntry(Admin admin) {
        super();
        
        String type = "admin";
        put("type", type);
        put("id", ""+admin.getId());
        put("username", admin.getUsername());
        put("master", admin.getMaster());
        put("salt", CryptoServiceProvider.b64encode(admin.getSalt())); //CHECK

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

    public byte[] getSalt() {
        return CryptoServiceProvider.b64decode(get("salt"));
    }
    
	public Admin.Header getHeader() {
		return new Admin.Header(Integer.parseInt(get("id")), get("username"));
	}

	public Admin toAdmin() {
		return new Admin(getUsername(), getSalt(), getMaster(), Integer.parseInt(getId()));
	}
    
}

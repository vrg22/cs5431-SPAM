// Keys: ID, username, hashed-salted master password
public class PasswordStorageEntry extends StorageEntry {
    // Note: master password here should already be hashed
    public PasswordStorageEntry(Client client) {
        super();
        
        String type = (client instanceof User) ? "user" :
        			  (client instanceof Admin) ? "admin" : "other";
        put("type", type);
        put("id", ""+client.getId());
        put("username", client.getUsername());
		put("iv", (client.getIV()==null) ? null : CryptoServiceProvider.b64encode(client.getIV()));
		put("salt", CryptoServiceProvider.b64encode(client.getSalt()));
        put("master", client.getMaster());
    }

    public String getType() {
        return get("type");
    }
    
    public int getId() {
        return Integer.parseInt(get("id"));
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

	public byte[] getIV() {
		return CryptoServiceProvider.b64decode(get("iv"));
	}

    public void setIV(String iv) {
        update("iv", iv);
    }
}

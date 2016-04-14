// Keys: user ID, username, hashed-salted master password
public class PasswordStorageEntry extends StorageEntry {
    // Note: master password here should already be hashed
    public PasswordStorageEntry(User user) {
        super();

        put("userid", ""+user.getID());
        put("username", user.getUsername());
		put("iv", CryptoServiceProvider.b64encode(user.getIV()));
		put("salt", CryptoServiceProvider.b64encode(user.getSalt()));
        put("master", user.getMaster());
    }

    public int getUserId() {
        return Integer.parseInt(get("userid"));
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

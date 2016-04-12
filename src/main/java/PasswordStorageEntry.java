// Keys: user ID, username, hashed-salted master password
public class PasswordStorageEntry extends StorageEntry {
    // Note: master password here should already be hashed
    public PasswordStorageEntry(User user) {
        super();

        put("userid", ""+user.getID());
        put("username", user.getUsername());
		//put("iv", new String(user.getIV()));
		put("salt", new String(user.getSalt()));
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
		return get("salt").getBytes();
	}

	public byte[] getIV() {
		return get("iv").getBytes();
	}
}

public class User {

	public static int MAX_USERS = 300;

	// Private fields
	private String username;
	private byte[] salt; //Salt used to hash master pwd
	private byte[] iv;
	private String master; //salted hash of master pwd
	private int id;
	// ETC.

	public User(String uname, byte[] salt, String pword, int ID, byte[] IV) {
		this.username = uname;
		this.salt = salt;
		this.master = pword; //TODO: take salted hash - WHERE?
		this.id = ID;
        this.iv = IV;
	}

	public int getID() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}


	//TODO: Add methods for updating a user's master password representation on record!

	public String getMaster() {
		return master;
	}

	public byte[] getSalt() {
		return salt;
	}

	public byte[] getIV() {
		return iv;
	}

//	public String getPassword() {
//		return password;
//	}

//	public void setPassword(String password) {
//		this.password = password;
//	}
}

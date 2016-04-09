public class User {

	public static final int MAX_USERS = 300; //TODO: Adjust this

	// Private fields
	private String username;
	private String master; //salted hash of master pwd
	private int id;
	// ETC.

	public User(String uname, String pword, int ID) {
		this.username = uname;
		this.master = pword; //TODO: take salted hash - WHERE?
		this.id = ID;
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
	
//	public String getPassword() {
//		return password;
//	}

//	public void setPassword(String password) {
//		this.password = password;
//	}
}

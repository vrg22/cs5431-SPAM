package server;

public class User {

	public static final int MAX_USERS = 300; //TODO: Adjust this
	
	// Private fields
	private String username;
	private String password;
	private int id;
	// ETC.
	
	public User(String uname, String pword, int ID) {
		this.username = uname;
		this.password = pword;
		this.id = ID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getID() {
		return id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	
	
}

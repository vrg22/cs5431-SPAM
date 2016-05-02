public class User {

	public static final int MAX_USERS = 300;

	// Private fields
	private String username;
	private byte[] salt; //Salt used to hash master pwd
	private byte[] iv;
	private String master; //salted hash of master pwd
	private int id;
	private String encPass;
	private String recovery;
	private byte[] reciv;
	// ETC.

	public User(String uname, byte[] salt, String pword, int ID, byte[] IV,
				String encPass, byte[] recIV, String recovery) {
		this.username = uname;
		this.salt = salt.clone();
		this.master = pword;
		this.id = ID;
        this.iv = IV.clone();
		this.reciv = recIV.clone();
		this.encPass = encPass;
		this.recovery = recovery;
	}

	public int getId() {
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
		return salt.clone();
	}

	public byte[] getIV() {
		return iv.clone();
	}

	public byte[] getRecIV() {
	  return reciv.clone();
	}

	public String getRecovery() {
	  return recovery;
	}

	public String getEncPass() {
	  return encPass;
	}
}

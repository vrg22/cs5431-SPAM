public class Admin extends Client {

	public static final int MAX_ADMINS = 5;

	public Admin(String uname, byte[] salt, String pword, int ID, byte[] IV) {
		super(uname, salt, pword, ID, IV);
	}
}
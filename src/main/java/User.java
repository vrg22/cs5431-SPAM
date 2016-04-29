public class User extends Client {

	public static final int MAX_USERS = 300;

	public User(String uname, byte[] salt, String pword, int ID, byte[] IV) {
		super(uname, salt, pword, ID, IV);
	}
}

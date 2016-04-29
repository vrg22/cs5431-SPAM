public class Admin extends Client {

	public static final int MAX_ADMINS = 5;

	public Admin(String uname, byte[] salt, String pword, int ID, byte[] IV,
            String encPass, byte[] recIV, String recovery,
            String twoFactorSecret) {
		super(uname, salt, pword, ID, IV, encPass, recIV, recovery, twoFactorSecret);
	}
}

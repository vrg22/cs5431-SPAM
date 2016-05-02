public class Admin extends Client {

	public static final int MAX_ADMINS = 5;

	public Admin(String uname, byte[] salt, String pword, int ID, byte[] IV) {
		super(uname, salt, pword, ID, IV);
	}
	
	public static class Header {
        private int id;
        private String username;

        public Header(int id, String username) {
            this.id = id;
            this.username = username;
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
    }
}
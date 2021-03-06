public class Account {

	// Private fields
    private Header header;
    private String username;
    private String password;

	public Account(int id, String name, String username, String password) {
		this.header = new Header(id, /*userId,*/ name);
        this.username = username;
        this.password = password;
	}

	public int getId() {
		return header.getId();
	}

    public String getName() {
        return header.getName();
    }

    public void setName(String name) {
        header.setName(name);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public static class Header {
        private int id;
        //private int userId;
        private String name;

        public Header(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
    		return id;
    	}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

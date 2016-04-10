public class Account {

	// Private fields
    private Header header;
    private String username;
    private String master;

	public Account(int id, /*int userId,*/ String name, String username, String password) {
		this.header = new Header(id, /*userId,*/ name);
        this.username = username;
        this.master = password;
	}

	public int getID() {
		return header.getID();
	}

//    public int getUserID() {
//        return header.getUserID();
//    }

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

	public String getMaster() {
		return master;
	}

	//TODO: Determine whether to take password or master here itself!
	public void setMaster(String password) {
		this.master = password;
	}

    public static class Header {
        private int id;
        //private int userId;
        private String name;

        public Header(int id, /*int userId,*/ String name) {
            this.id = id;
            //this.userId = userId;
            this.name = name;
        }

        public int getID() {
    		return id;
    	}

//        public int getUserID() {
//            return userId;
//        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

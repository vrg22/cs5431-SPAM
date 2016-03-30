public class ClientService {
    private boolean hasUser;

    public ClientService() {
        hasUser = false;
    }

    // Return whether a user is logged in
    // TODO: implement
    public boolean hasUser() {
        return hasUser;
    }

    // TODO: replace with an actual method to login a user
    public void setUser() {
        hasUser = true;
    }

    // Create a new SPAM user
    // TODO: implement
    public void createUser(String email, String password) {

    }
}

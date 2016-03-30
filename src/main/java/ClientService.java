public class ClientService {
    private boolean hasUser;

    public ClientService() {
        hasUser = false;
    }

    public boolean hasUser() {
        return hasUser;
    }

    public void setUser() {
        hasUser = true;
    }

    public void createUser(String email, String password) {

    }
}

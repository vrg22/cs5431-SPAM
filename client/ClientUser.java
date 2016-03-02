package client;

import java.util.*;

public class ClientUser extends Client {

    private String MENU_PREFIX = "USER-";

    public ClientUser() {
        super();

        goToMenu("Home");
    }

    public static void main(String[] args) {
        Client.printWelcome();

        ClientUser client = new ClientUser();
        client.run();
    }
}

package client;

import java.util.*;

public class ClientUser extends Client {

    public ClientUser(String host, int port) {
        super(host, port);

        goToMenu("MainMenu");
    }

    public static void main(String[] args) {
    	String host = null;
    	int port = 0;
    	try {
    		host = args[0];
    		port = Integer.parseInt(args[1]);
    	} catch(Exception e) {
    		clientOutput.println("Please specify a host and port.");
    		return;
    	}

        Client.printWelcome();

        ClientUser client = new ClientUser(host, port);
        client.run();
    }
}

package client.menus;

import java.util.*;

import client.Client;
import client.Menu;
import communications.CommClient;

public class MainMenu extends Menu {

    public MainMenu(Client client, CommClient comm) {
        super("MainMenu", client, comm);

        MenuOption register = new MenuOption("Register for SPAM",
            "UserRegister-Email");
        MenuOption login = new MenuOption("Log in",
            "Login-Email");

        ArrayList<MenuOption> options = new ArrayList<>();
        options.add(register);
        options.add(login);

        this.title = "MainMenu";
        this.options = options;
        this.prompt = "Please select an option (or -1 to quit): ";
    }
}

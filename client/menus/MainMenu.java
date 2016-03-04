package client.menus;

import java.util.*;

import client.Menu;

public class MainMenu extends Menu {

    public MainMenu() {
        super("Home");

        MenuOption register = new MenuOption("Register for SPAM",
            "UserRegister-Name");
        MenuOption login = new MenuOption("Log in",
            "Login-Email");

        ArrayList<MenuOption> options = new ArrayList<>();
        options.add(register);
        options.add(login);

        this.title = "Home";
        this.options = options;
        this.prompt = "Please select an option (1 or 2): ";
    }
}

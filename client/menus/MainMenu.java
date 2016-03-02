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

    @Override
    public String handleInput(String input) {
        try {
            int i = Integer.parseInt(input);
            if (i >= 1 && i <= this.options.size()) {
                MenuOption option = this.options.get(i - 1);

                if (option.hasAction()) {
                    option.getAction().run();
                }

                return option.getNextMenuIdentifier();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }
}

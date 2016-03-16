package client.menus;

import client.Menu;

public class UserRegisterName extends Menu {
    public UserRegisterName() {
        super("UserRegister-Name");
        this.prompt = "Please enter your full name: ";
    }

    @Override
    public String handleInput(String input) {        
        // TODO: store name

        return "UserRegister-Email";
    }
}

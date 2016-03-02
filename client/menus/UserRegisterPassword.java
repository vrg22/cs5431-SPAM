package client.menus;

import client.Menu;

public class UserRegisterPassword extends Menu {

	public UserRegisterPassword() {
        super("UserRegister-Password");
        this.prompt = "Please type a master password: ";
    }

	@Override
	public String handleInput(String input) {		
		// TODO: store password

        return "Home";
	}

}

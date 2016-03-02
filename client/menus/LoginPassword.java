package client.menus;

import client.Menu;

public class LoginPassword extends Menu {

	public LoginPassword() {
        super("Login-Password");
        this.prompt = "Please enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: check if valid email/password

		return "UserVault";
	}

}

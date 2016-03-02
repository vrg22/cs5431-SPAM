package client.menus;

import client.Menu;

public class LoginEmail extends Menu {

	public LoginEmail() {
        super("Login-Email");
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
        // TODO: keep track of email
        
		return "Login-Password";
	}

}

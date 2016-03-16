package client.menus;

import client.*;
import communications.Message;
import communications.Message.*;

public class LoginEmail extends Menu {

	public LoginEmail() {
        super("Login-Email");
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
        client.updateUsername(input);

		return "Login-Password";
	}

}

package client.menus;

import client.*;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class LoginEmail extends Menu {

	public LoginEmail(Client client, CommClient comm) {
        super("Login-Email", client, comm);
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
        client.updateUsername(input);

		return "Login-Password";
	}

}

package client.menus;

import client.Menu;
import communications.Message.*;

public class LoginPassword extends Menu {

	public LoginPassword() {
        super("Login-Password");
        this.prompt = "Please enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		LoginMessage login = (LoginMessage)comm.getSaved();
		login.updatePassword(input);
		comm.send(login);

		return "UserVault";
	}

}

package client.menus;

import client.Menu;

public class LoginPassword extends Menu {

	public LoginPassword() {
        super("Login-Password");
        this.prompt = "Please enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		LoginMessage login = comm.getSaved();
		login.updatePassword(input);
		comm.send(login);

		return "UserVault";
	}

}

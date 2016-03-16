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
		client.updatePassword(input);
		
		LoginMessage login = new LoginMessage(client.getUsername(), client.getPassword());
		comm.send(login);

		Response response = (Response)comm.receive();
		String code = response.getResponseCode();
		if (code.equals("OK")) {
			return "UserVault";
		}

		return "LoginEmail";
	}

}

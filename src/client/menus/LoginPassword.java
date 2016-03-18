package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class LoginPassword extends Menu {

	public LoginPassword(Client client, CommClient comm) {
        super("Login-Password", client, comm);
        this.prompt = "Please enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		client.updatePassword(input);

		LoginMessage login = new LoginMessage(client.getUsername(), client.getPassword());
		comm.send(login);

		Message responseMsg = comm.receive();
		if (responseMsg instanceof Response) {
			Response response = (Response)responseMsg;
			if (validateResponse(response)) {
				String code = response.getResponseCode();
				if (code.equals("OK")) {
					client.getClientOutput().println("Login successful");
		
					return "UserVault";
				}
			}
		}
	
		client.getClientOutput().println("Login unsuccessful");

		return "LoginEmail";
	}

}

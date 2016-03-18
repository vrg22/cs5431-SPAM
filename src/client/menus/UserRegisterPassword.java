package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class UserRegisterPassword extends Menu {

	public UserRegisterPassword(Client client, CommClient comm) {
        super("UserRegister-Password", client, comm);
        this.prompt = "Please type a master password: ";
    }

	@Override
	public String handleInput(String input) {
		Message saved = comm.getSaved();
		if (saved instanceof RegisterMessage) {
			RegisterMessage register = (RegisterMessage)saved;
			register.updatePassword(input);
			comm.send(register);
	
			Message responseMsg = comm.receive();
			if (responseMsg instanceof Response) {
				Response response = (Response)responseMsg;
				if (validateResponse(response)) {
					String code = response.getResponseCode();
					if (code.equals("OK")) {
						client.getClientOutput().println("Register successful");
			
						// Log in with new credentials
						client.updateUsername(response.getUsername());
						client.updatePassword(response.getPassword());
			
						return "UserVault";
					}
				}
			}
		}

		client.getClientOutput().println("Register unsuccessful");

        return "MainMenu";
	}

}

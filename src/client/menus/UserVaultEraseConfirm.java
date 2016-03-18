package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class UserVaultEraseConfirm extends Menu {

	public UserVaultEraseConfirm(Client client, CommClient comm) {
        super("UserVault-Erase-Confirm", client, comm);

		this.prompt = "Are you sure you want to delete your account? (y/N):";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("y")) {
			ObliterateMessage obliterate = new ObliterateMessage(client.getUsername(), client.getPassword());
			comm.send(obliterate);

			Message responseMsg = comm.receive();
			if (responseMsg instanceof Response) {
				Response response = (Response)responseMsg;
				if (validateResponse(response)) {
					String code = response.getResponseCode();
					if (code.equals("OK")) {
						client.updateUsername(null);
						client.updatePassword(null);
						
						client.getClientOutput().println("Account deleted");
						return "MainMenu";
					}
				}
			}
		}

		client.getClientOutput().println("Account not deleted");

		return "UserVault";
	}
}

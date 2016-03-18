package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message.*;

public class UserVaultErasePassword extends Menu {

	public UserVaultErasePassword(Client client, CommClient comm) {
        super("UserVault-Erase-Password", client, comm);

		this.prompt = "Please re-enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		if (!input.equals(client.getPassword())) {
			client.getClientOutput().println("Invalid password");
			return "UserVault";
		}

		return "UserVault-Erase-Confirm";
	}
}

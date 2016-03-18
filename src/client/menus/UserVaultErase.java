package client.menus;

import client.Client;
import client.Menu;
import communications.*;
import communications.Message.*;

public class UserVaultErase extends Menu {

	public UserVaultErase(Client client, CommClient comm) {
        super("UserVault-Erase", client, comm);

		this.title = "WARNING: Erasing your vault will permanently delete"
		 	+ " your entire SPAM account, including all the credentials stored"
			+ " in it. This action cannot be undone.";
		this.prompt = "Please re-enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
		// Validate email address
		if (!input.equals(client.getUsername())) {
			client.getClientOutput().println("Invalid email address");
			return "UserVault";
		}

		return "UserVault-Erase-Password";
	}
}

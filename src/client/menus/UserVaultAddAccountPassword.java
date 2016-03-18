package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;

public class UserVaultAddAccountPassword extends Menu {

	public UserVaultAddAccountPassword(Client client, CommClient comm) {
        super("UserVault-AddAccount-Password", client, comm);

        this.prompt = "Please enter your password for the account: ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: store account

		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

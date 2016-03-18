package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;

public class UserVaultAddAccountUsername extends Menu {

	public UserVaultAddAccountUsername(Client client, CommClient comm) {
        super("UserVault-AddAccount-Username", client, comm);

        this.prompt = "Please enter your username for the account"
			+ " (or 'return' to leave blank): ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: store account

		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;

public class UserVaultAddAccountName extends Menu {

	public UserVaultAddAccountName(Client client, CommClient comm) {
        super("UserVault-AddAccount-Name", client, comm);

        this.prompt = "Please name this account: ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: store account

		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

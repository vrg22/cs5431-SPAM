package client.menus;

import client.Menu;

public class UserVaultAddAccountName extends Menu {

	public UserVaultAddAccountName() {
        super("UserVault-AddAccount-Name");

        this.prompt = "Please name this account: ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: store account

		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

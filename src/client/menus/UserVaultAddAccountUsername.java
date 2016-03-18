package client.menus;

import client.Menu;

public class UserVaultAddAccountUsername extends Menu {

	public UserVaultAddAccountUsername() {
        super("UserVault-AddAccount-Username");

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

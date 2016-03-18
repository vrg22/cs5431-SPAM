package client.menus;

import client.Menu;

public class UserVaultAddAccountPassword extends Menu {

	public UserVaultAddAccountPassword() {
        super("UserVault-AddAccount-Password");

        this.prompt = "Please enter your password for the account: ";
    }

	@Override
	public String handleInput(String input) {
		// TODO: store account

		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

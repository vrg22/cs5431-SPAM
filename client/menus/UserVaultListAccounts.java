package client.menus;

import client.Menu;

public class UserVaultListAccounts extends Menu {

	public UserVaultListAccounts() {
        super("UserVault-ListAccounts");

		// TODO: Add one option for each stored account

		this.title = "Stored Accounts";
        this.prompt = "Please select an account to view, or -1 to go back: ";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("-1")) return "UserVault";

		return super.handleInput(input);
	}
}

package client.menus;

import client.Menu;
import client.actions.EraseVaultAction;

public class UserVaultErase extends Menu {

	public UserVaultErase() {
        super("UserVault-Erase");

		this.title = "WARNING: Erasing your vault will permanently delete"
		 	+ " your entire SPAM account, including all the credentials stored"
			+ " in it. This action cannot be undone.";
		this.prompt = "Are you sure (y/N)?";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("y")) {
			(new EraseVaultAction()).run();

			return "UserVault-Erased";
		}

		return "UserVault-CanceledErase";
	}
}

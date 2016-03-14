package client.menus;

import client.Menu;

public class UserVaultEraseConfirm extends Menu {

	public UserVaultEraseConfirm() {
        super("UserVault-Erase-Confirm");

		this.prompt = "Are you sure you want to delete your account? (y/N):";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("y")) {
			ObliterateMessage obliterate = comm.getSaved();
			comm.send(obliterate);

			return "MainMenu";
		}

		return "UserVault";
	}
}

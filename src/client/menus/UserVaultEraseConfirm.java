package client.menus;

import client.Menu;
import communications.Message.*;

public class UserVaultEraseConfirm extends Menu {

	public UserVaultEraseConfirm() {
        super("UserVault-Erase-Confirm");

		this.prompt = "Are you sure you want to delete your account? (y/N):";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("y")) {
			ObliterateMessage obliterate = (ObliterateMessage)comm.getSaved();
			comm.send(obliterate);

			return "MainMenu";
		}

		return "UserVault";
	}
}

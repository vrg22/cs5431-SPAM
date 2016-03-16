package client.menus;

import client.Menu;
import communications.Message.*;

public class UserVaultErasePassword extends Menu {

	public UserVaultErasePassword() {
        super("UserVault-Erase-Password");

		this.prompt = "Please re-enter your master password: ";
    }

	@Override
	public String handleInput(String input) {
		if (!input.equals(client.getPassword())) return "UserVault";

		return "UserVault-Erase-Confirm";
	}
}

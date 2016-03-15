package client.menus;

import client.Menu;
import communications.*;
import communications.Message.*;

public class UserVaultErase extends Menu {

	public UserVaultErase() {
        super("UserVault-Erase");

		this.title = "WARNING: Erasing your vault will permanently delete"
		 	+ " your entire SPAM account, including all the credentials stored"
			+ " in it. This action cannot be undone.";
		this.prompt = "Please re-enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
		Message obliterate = new ObliterateMessage(input, null);
		comm.save(obliterate);

		return "UserVault-Erase-Password";
	}
}

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
		ObliterateMessage obliterate = (ObliterateMessage)comm.getSaved();
		obliterate.updatePassword(input);
		comm.save(obliterate);

		return "UserVault-Erase-Confirm";
	}
}

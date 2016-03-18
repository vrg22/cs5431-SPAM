package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message.EditIdMessage;

public class UserVaultAddAccountPassword extends Menu {

	public UserVaultAddAccountPassword(Client client, CommClient comm) {
        super("UserVault-AddAccount-Username", client, comm);
		
        this.prompt = "Please enter your password for the account: ";
    }

	@Override
	public String handleInput(String input) {
		EditIdMessage edit = (EditIdMessage)comm.getSaved();
		edit.updatePassword(input);

		return "UserVault-AddAccount-Name";
	}
}

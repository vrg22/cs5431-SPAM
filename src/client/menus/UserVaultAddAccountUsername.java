package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.EditIdMessage;

public class UserVaultAddAccountUsername extends Menu {

	public UserVaultAddAccountUsername(Client client, CommClient comm) {
        super("UserVault-AddAccount-Username", client, comm);

        this.prompt = "Please enter your username for the account"
			+ " (or 'return' to leave blank): ";
    }

	@Override
	public String handleInput(String input) {
		Message edit = new EditIdMessage(input, null, 0, null);
		comm.save(edit);

		return "UserVault-AddAccount-Password";
	}
}

package client.menus;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.EditIdMessage;

public class UserVaultAddAccountName extends Menu {

	public UserVaultAddAccountName(Client client, CommClient comm) {
        super("UserVault-AddAccount-Name", client, comm);

        this.prompt = "Please name this account: ";
    }

	@Override
	public String handleInput(String input) {
		// EditIdMessage edit = (EditIdMessage)comm.getSaved();
		// TODO: Finish building message, send to server, handle result
		
		client.getClientOutput().println("Not yet implemented");
		return "UserVault";
	}
}

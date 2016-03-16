package client.menus;

import java.util.ArrayList;

import client.Menu;
import communications.Message.*;

public class UserVaultAccountDetails extends Menu {

	public UserVaultAccountDetails() {
        super("UserVault-AccountDetails");

		RetrieveIdMessage retrieve = new RetrieveIdMessage(client.getUsername(),
			client.getPassword(), client.getCurrentRecordId());
		comm.send(retrieve);
		RetrieveIdResponse response = (RetrieveIdResponse)comm.receive();
		Record record = response.getRecord();
		this.title = String.format("%s:\nUsername: %s\nPassword: %s",
			record.get("name"), record.get("username"), record.get("password"));

        this.prompt = "Press any key to go back: ";
    }

	@Override
	public String handleInput(String input) {
		return "UserVault";
	}
}

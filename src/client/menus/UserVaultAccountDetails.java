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
		if (validateResponse(response)) {
			Record record = response.getRecord();
			this.title = String.format("%s:\nUsername: %s\nPassword: %s",
				record.get("name"), record.get("username"), record.get("password"));
		} else {
			this.title = "Error retrieving data from server";
		}

        this.prompt = "Press any key to go back: ";
    }
	
	@Override
	protected boolean validateResponse(Response response) {
		if (!super.validateResponse(response)) return false;
		
		Record record = ((RetrieveIdResponse)response).getRecord();
		return record != null && record.contains("name")
				&& record.contains("username") && record.contains("password");
	}

	@Override
	public String handleInput(String input) {
		return "UserVault";
	}
}

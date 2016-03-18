package client.menus;

import java.util.ArrayList;

import client.Client;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class UserVaultAccountDetails extends Menu {

	public UserVaultAccountDetails(Client client, CommClient comm) {
        super("UserVault-AccountDetails", client, comm);

		RetrieveIdMessage retrieve = new RetrieveIdMessage(client.getUsername(),
			client.getPassword(), client.getCurrentRecordId());
		comm.send(retrieve);
		
		this.prompt = "Press any key to go back: ";
		
		Message responseMsg = comm.receive();
		if (responseMsg instanceof RetrieveIdResponse) {
			RetrieveIdResponse response = (RetrieveIdResponse)responseMsg;
			if (validateResponse(response)) {
				Record record = response.getRecord();
				this.title = String.format("%s:%nUsername: %s%nPassword: %s",
					record.get("name"), record.get("username"), record.get("password"));
				return;
			}
		}
		
		this.title = "Error retrieving data from server";
    }
	
	@Override
	protected boolean validateResponse(Response response) {
		if (!super.validateResponse(response)
				|| !(response instanceof RetrieveIdResponse)) {
			return false;
		}
		
		Record record = ((RetrieveIdResponse)response).getRecord();
		return record != null && record.contains("name")
				&& record.contains("username") && record.contains("password");
	}

	@Override
	public String handleInput(String input) {
		return "UserVault";
	}
}

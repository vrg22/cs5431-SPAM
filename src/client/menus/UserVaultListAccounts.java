package client.menus;

import java.util.ArrayList;

import client.Menu;
import communications.Message.*;

public class UserVaultListAccounts extends Menu {

	public UserVaultListAccounts() {
        super("UserVault-ListAccounts");
        
        ListingMessage listingRequest = new ListingMessage(client.getUsername(), client.getPassword());
		comm.send(listingRequest);
		ListingResponse response = (ListingResponse)comm.receive();
		if (validateResponse(response)) {
		
			// List each account as an option
			ArrayList<MenuOption> options = new ArrayList<>();
			for (Record rec : response.getResponseRecords()) {
				MenuOption option = new MenuOption(rec.get("name"),
		            "UserVault-AccountDetails", rec);
		        options.add(option);
			}
			this.options = options;
	
			this.title = "Stored Accounts";
			if (response.getResponseRecords().size() == 0) {
				this.title += "\n(None)";
			}
			this.prompt = "Please select an account to view, or -1 to go back: ";
		} else {
			this.title = "Error retrieving accounts";
			this.prompt = "Press any key to go back: ";
		}
    }
	
	@Override
	protected boolean validateResponse(Response response) {
		if (!super.validateResponse(response)) return false;
		
		ArrayList<Record> records = ((ListingResponse)response).getResponseRecords();
		if (records == null) return false;
		
		for (Record record : records) {
			if (record == null || !record.contains("id") || !record.contains("name")) {
				return false;
			}
		}
		
		return true;
	}

	@Override
	public String handleInput(String input) {
		if (this.options == null || input.equals("-1")) return "UserVault";

		try {
            int i = Integer.parseInt(input);
            if (i >= 1 && i <= this.options.size()) {
                MenuOption option = this.options.get(i - 1);

				client.setCurrentRecordId(Integer.parseInt(option.getRecord().get("id")));

                return option.getNextMenuIdentifier();
            }
        } catch (Exception e) {
        }

		client.getClientOutput().println("Invalid option");

		return null;
	}
}

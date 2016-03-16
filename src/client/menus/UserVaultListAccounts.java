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
		ArrayList<MenuOption> options = new ArrayList<>();
		for (Record rec : response.getResponseRecords()) {
			MenuOption option = new MenuOption(rec.get("name"),
	            "UserVault-AccountDetails", rec);
	        options.add(option);
		}
		this.options = options;

		this.title = "Stored Accounts";
        this.prompt = "Please select an account to view, or -1 to go back: ";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("-1")) return "UserVault";

		try {
            int i = Integer.parseInt(input);
            if (i >= 1 && i <= this.options.size()) {
                MenuOption option = this.options.get(i - 1);

				client.setCurrentRecordId(Integer.parseInt(option.getRecord().get("id")));

                return option.getNextMenuIdentifier();
            }
        } catch (Exception e) {
        }

		return null;
	}
}

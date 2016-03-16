package client.menus;

import client.Menu;

public class UserVaultListAccounts extends Menu {

	public UserVaultListAccounts() {
        super("UserVault-ListAccounts");

		// TODO: Add one option for each stored account
		ListingMessage listingRequest = new ListingMessage(username, password);
		comm.send(listingRequest);
		ListingResponse response = (ListingResponse)comm.receive();
		ArrayList<MenuOption> options = new ArrayList<>();
		for (Record rec : response.getResponseRecords()) {
			MenuOption option = new MenuOption(rec.get("name"),
	            "UserVault-AccountDetails");
			// TODO: store the record id with the option
	        options.add(register);
		}
		this.options = options;

		this.title = "Stored Accounts";
        this.prompt = "Please select an account to view, or -1 to go back: ";
    }

	@Override
	public String handleInput(String input) {
		if (input.equals("-1")) return "UserVault";

		return super.handleInput(input);
	}
}

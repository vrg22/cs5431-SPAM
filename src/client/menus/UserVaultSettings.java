package client.menus;

import java.util.ArrayList;

import client.Menu;

public class UserVaultSettings extends Menu {

	public UserVaultSettings() {
        super("UserVault-Settings");

		ArrayList<MenuOption> options = new ArrayList<>();
		options.add(new MenuOption("Update my email address",
			"UserVault-Settings-ChangeEmail"));
		options.add(new MenuOption("Change my master password",
			"UserVault-Settings-ChangePassword"));
		options.add(new MenuOption("Back to main menu",
			"UserVault"));

		this.title = "Account settings";
		this.options = options;
        this.prompt = "Please select an option: ";
    }
	
	@Override
	public String handleInput(String input) {
		client.getClientOutput().println("Not yet implemented.");
		return "UserVault";
	}
}

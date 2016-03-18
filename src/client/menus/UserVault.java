package client.menus;

import java.util.ArrayList;

import client.Client;
import client.Menu;
import communications.CommClient;

public class UserVault extends Menu {

	public UserVault(Client client, CommClient comm) {
        super("UserVault", client, comm);

		ArrayList<MenuOption> options = new ArrayList<>();
		options.add(new MenuOption("List my accounts",
			"UserVault-ListAccounts"));
		options.add(new MenuOption("Add new account",
			"UserVault-AddAccount-Username"));
		options.add(new MenuOption("Manage account settings",
			"UserVault-Settings"));
		options.add(new MenuOption("Erase my entire vault", "UserVault-Erase"));

		this.title = "Welcome to your vault.";
		this.options = options;
        this.prompt = "Please select an option: ";
    }
}

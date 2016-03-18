package client.menus;

import java.util.Scanner;
import client.*;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class LoginEmail extends Menu {

	public LoginEmail(Client client, CommClient comm) {
		super("Login-Email", client, comm);
		this.prompt = "Please enter your email address (or -1 to quit): ";
	}

	@Override
	public String handleInput(String input) {
		Scanner sc = new Scanner(input.trim());
		if (sc.hasNextInt()) {
			int option = Integer.parseInt(input);
			if (option == -1) return "quit";
		}
		sc.close();

        client.updateUsername(input);

		return "Login-Password";
	}

}

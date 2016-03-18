package client.menus;

import client.Client;
import java.util.Scanner;
import client.Menu;
import communications.CommClient;
import communications.Message;
import communications.Message.*;

public class LoginPassword extends Menu {

	public LoginPassword(Client client, CommClient comm) {
		super("Login-Password", client, comm);
		this.prompt = "Please enter your master password (or -1 to quit): ";
	}

	@Override
	public String handleInput(String input) {
		Scanner sc = new Scanner(input.trim());
		if (sc.hasNextInt()) {
			int option = Integer.parseInt(input);
			if (option == -1) return "quit";
		}
		sc.close();

		client.updatePassword(input);

		LoginMessage login = new LoginMessage(client.getUsername(), client.getPassword());
		comm.send(login);

		Message responseMsg = comm.receive();
		if (responseMsg instanceof Response) {
			Response response = (Response)responseMsg;
			if (validateResponse(response)) {
				String code = response.getResponseCode();
				if (code.equals("OK")) {
					client.getClientOutput().println("Login successful");
		
					return "UserVault";
				}
			}
		}
	
		client.getClientOutput().println("Login unsuccessful");

		return "Login-Email";
	}

}

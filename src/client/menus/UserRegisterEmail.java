package client.menus;

import client.Client;
import java.util.Scanner;
import client.Menu;
import communications.*;
import communications.Message.*;
import java.util.regex.Pattern;

public class UserRegisterEmail extends Menu {
	final static Pattern emailPattern = Pattern.
		compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

	public boolean isEmailValid(final String email) {
		return ((emailPattern.matcher(email).matches()) ? true : false);
	}

	public UserRegisterEmail(Client client, CommClient comm) {
		super("UserRegister-Email", client, comm);
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

		if (isEmailValid(input)) {
			Message register = new RegisterMessage(input, null);
			comm.save(register);

			return "UserRegister-Password";
		} else {
			client.getClientOutput().println("Invalid email address");
			return "UserRegister-Email";
		}

	}
}

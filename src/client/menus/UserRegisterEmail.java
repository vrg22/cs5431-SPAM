package client.menus;

import client.Client;
import client.Menu;
import communications.*;
import communications.Message.*;

public class UserRegisterEmail extends Menu {

	public UserRegisterEmail(Client client, CommClient comm) {
        super("UserRegister-Email", client, comm);
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
		Message register = new RegisterMessage(input, null);
		comm.save(register);

		return "UserRegister-Password";
	}

}

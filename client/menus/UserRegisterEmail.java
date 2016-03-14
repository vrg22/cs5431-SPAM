package client.menus;

import client.Menu;

public class UserRegisterEmail extends Menu {

	public UserRegisterEmail() {
        super("UserRegister-Email");
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
		Message register = new RegisterMessage(input, null);
		comm.save(register);

		return "UserRegister-Password";
	}

}

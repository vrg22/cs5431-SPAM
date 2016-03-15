package client.menus;

import client.Menu;
import communications.Message.*;

public class UserRegisterPassword extends Menu {

	public UserRegisterPassword() {
        super("UserRegister-Password");
        this.prompt = "Please type a master password: ";
    }

	@Override
	public String handleInput(String input) {
		RegisterMessage register = (RegisterMessage)comm.getSaved();
		register.updatePassword(input);
		comm.send(register);

        return "Home";
	}

}

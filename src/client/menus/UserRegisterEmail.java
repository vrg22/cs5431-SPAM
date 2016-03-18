package client.menus;

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

	public UserRegisterEmail() {
        super("UserRegister-Email");
        this.prompt = "Please enter your email address: ";
    }

	@Override
	public String handleInput(String input) {
		if (isEmailValid(input)) {
			Message register = new RegisterMessage(input, null);
			comm.save(register);

			return "UserRegister-Password";
		} else {
			return "UserRegister-Email";
		}
	}

}

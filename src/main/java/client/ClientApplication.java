import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public abstract class ClientApplication
{
	public static final String HTTPS_ROOT = "https://ec2-52-91-199-182.compute-1.amazonaws.com:4567";
	protected static final int PASSWORD_LENGTH = 12;
    protected Gson gson;

	/**
	 * Attempt to log in with specified credentials
	 *
	 * @return Was login successful
	 */
	public abstract boolean login(String email, String password, String twoFactorCode);

    public abstract void logout(boolean expired);

    /**
     * Register new user, and log in with the new user.
     *
     * @return Was user successfully registered
     */
    public abstract boolean register(String email, String password,
            String recovery, String twoFactorSecret);

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
    }
}

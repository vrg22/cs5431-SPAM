package client;


import java.io.Console;
import java.io.PrintStream;
import java.util.*;

import client.menus.*;
import communications.*;

/**
 * Client application
 */
public abstract class Client {

	protected int SUCCESS_CODE = 0;
	protected int QUIT_CODE = -1;

    private Map<String, Menu> cachedMenus;
    protected Menu currentMenu;
	protected int currentRecordId;

	protected CommClient comm;

	protected static final PrintStream clientOutput = System.out;

	protected String username;
	protected String password;

    public Client(String host, int port) {
        cachedMenus = new HashMap<>();

		comm = new CommClient(host, port);
    }

	public void run() {
        Scanner in = new Scanner(System.in);
        Console console = System.console();
        comm.makeConnection(); //TODO: Should this be here or elsewhere?

        while (true) {
            clientOutput.print(currentMenu);

            String input = null;
            if (console != null &&
            		(currentMenu.identifier.equals("Login-Password") ||
            		currentMenu.identifier.equals("UserRegister-Password") ||
            		currentMenu.identifier.equals("UserVault-AddAccount-Password") ||
            		currentMenu.identifier.equals("UserVault-Erase-Password"))) {
            	input = new String(console.readPassword());
            } else {
            	input = in.nextLine();
            }
            clientOutput.println();

            // Handle user input
            String newMenu = currentMenu.handleInput(input);
            if (goToMenu(newMenu) == QUIT_CODE) break;
        }

        in.close();
    }

    protected int goToMenu(String identifier) {
        if (identifier == null) return SUCCESS_CODE;
        else if (identifier.equals("quit")) return QUIT_CODE;

        currentMenu = getMenuWithIdentifier(identifier);
        return SUCCESS_CODE;
    }

    protected Menu getMenuWithIdentifier(String identifier) {
        if (cachedMenus.containsKey(identifier)) {
            return cachedMenus.get(identifier);
		}

        try {
			Menu menu = (Menu) Menu.getClassForIdentifier(identifier).newInstance();
	        cachedMenus.put(identifier, menu);
	        return menu;
        } catch(InstantiationException e) {
        	return null;
        } catch(IllegalAccessException e) {
        	return null;
        }
    }

    public void updateUsername(String username) {
    	this.username = username;
    }

    public String getUsername() {
    	return this.username;
    }

    public void updatePassword(String password) {
    	this.password = password;
    }

    public String getPassword() {
    	return this.password;
    }

    public void setCurrentRecordId(int id) {
    	this.currentRecordId = id;
    }

	public int getCurrentRecordId() {
		return this.currentRecordId;
	}

	public PrintStream getClientOutput() {
		return clientOutput;
	}

    protected static void printWelcome() {
        clientOutput.println("\n"
            + "                   Welcome to\n"
            + " .oooooo..o\n"
            + "d8P'    `Y8\n"
            + "Y88bo.      oo.ooooo.   .oooo.   ooo. .oo.  .oo.\n"
            + " `\"Y8888o.   888' `88b `P  )88b  `888P\"Y88bP\"Y88b\n"
            + "     `\"Y88b  888   888  .oP\"888   888   888   888\n"
            + "oo     .d8P  888   888 d8(  888   888   888   888\n"
            + "8\"\"88888P'   888bod8P' `Y888\"\"8o o888o o888o o888o\n"
            + "             888\n"
            + "            o888o\n");
    }
}

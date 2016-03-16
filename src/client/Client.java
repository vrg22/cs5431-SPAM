package client;


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

	protected CommClient comm;

    public Client(String host, int port) {
        cachedMenus = new HashMap<>();

		comm = new CommClient(host, port);
    }

	public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print(currentMenu);

            // TODO: handle case where in.nextLine() throws exception
            String input = in.nextLine();
            System.out.println();

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
			menu.setComm(comm);
	        cachedMenus.put(identifier, menu);
	        return menu;
        } catch(InstantiationException e) {
        	return null;
        } catch(IllegalAccessException e) {
        	return null;
        }
    }

    protected static void printWelcome() {
        System.out.println("\n"
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

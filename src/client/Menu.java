package client;


import java.util.*;

import client.menus.*;
import communications.*;
import communications.Message.Record;

public abstract class Menu {
    protected String identifier;
    protected String title;
    protected ArrayList<MenuOption> options;
    protected String prompt;
    protected Client client;
    protected CommClient comm;

    private static final Map<String, Class<?>> identifierClassMap = createIdentifierClassMap();
    private static Map<String, Class<?>> createIdentifierClassMap() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("Login-Email", LoginEmail.class);
        map.put("Login-Password", LoginPassword.class);
        map.put("MainMenu", MainMenu.class);
        map.put("UserRegister-Email", UserRegisterEmail.class);
        map.put("UserRegister-Password", UserRegisterPassword.class);
        map.put("UserVault", UserVault.class);
        map.put("UserVault-AddAccount-Name",  UserVaultAddAccountName.class);
        map.put("UserVault-AddAccount-Password", UserVaultAddAccountPassword.class);
        map.put("UserVault-AddAccount-Username", UserVaultAddAccountUsername.class);
        map.put("UserVault-Erase", UserVaultErase.class);
        map.put("UserVault-Erase-Password", UserVaultErasePassword.class);
        map.put("UserVault-Erase-Confirm", UserVaultEraseConfirm.class);
        map.put("UserVault-ListAccounts", UserVaultListAccounts.class);
        map.put("UserVault-Settings", UserVaultSettings.class);
        return Collections.unmodifiableMap(map);
    }

    public Menu(String identifier) {
        this.identifier = identifier;
    }

    public void setComm(CommClient comm) {
        this.comm = comm;
    }

    public void setClient(Client client) {
    	this.client = client;
    }

    /**
     * Process user's input
     * Default: Look for appropriate action based on selected menu option
     *
     * @return name of next menu to display (or null to remain on same menu)
     */
    public String handleInput(String input) {
        try {
            int i = Integer.parseInt(input);
            if (i >= 1 && i <= this.options.size()) {
                MenuOption option = this.options.get(i - 1);

                return option.getNextMenuIdentifier();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    public String toString() {
        String out = "";

        if (this.title != null) out += this.title + ":\n";

        MenuOption option;
        if (options != null) {
            for (int i = 0; i < options.size() - 1; i++) {
                option = options.get(i);
                out += (i + 1) + ") " + option.getTitle() + "\n";
            }
            if (options.size() >= 1) {
                option = options.get(options.size() - 1);
                out += options.size() + ") " + option.getTitle();
            }

            if (this.prompt != null) out += "\n";
        }
        if (this.prompt != null) {
            out += this.prompt;
        }

        return out;
    }

    public static Class<?> getClassForIdentifier(String identifier) {
    	if (identifierClassMap.containsKey(identifier)) {
    		return identifierClassMap.get(identifier);
    	}

    	throw new NoSuchElementException("No menu with that identifier exists.");
    }


    public class MenuOption {
        private String title;
        private String nextMenuIdentifier;
        private Record record;

        public MenuOption(String title, String nextMenuIdentifier) {
            this.title = title;
            this.nextMenuIdentifier = nextMenuIdentifier;
        }

        public MenuOption(String title, String nextMenuIdentifier, Record record) {
            this.title = title;
            this.nextMenuIdentifier = nextMenuIdentifier;
            this.record = record;
        }

        public MenuOption(String title) {
            this.title = title;
        }

        public String getTitle() {
            return this.title;
        }

        public String getNextMenuIdentifier() {
            return this.nextMenuIdentifier;
        }

        public boolean hasNextMenu() {
            return this.nextMenuIdentifier != null;
        }

        public Record getRecord() {
            return this.record;
        }
    }
}

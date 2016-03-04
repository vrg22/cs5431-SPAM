package client;

import java.util.*;

import client.menus.*;

public abstract class Menu {
    protected String identifier;
    protected String title;
    protected ArrayList<MenuOption> options;
    protected String prompt;

    private static final Map<String, Class<?>> identifierClassMap = createIdentifierClassMap();
    private static Map<String, Class<?>> createIdentifierClassMap() {
        Map<String, Class<?>> map = new HashMap<>();
        map.put("Login-Email", LoginEmail.class);
        map.put("Login-Password", LoginPassword.class);
        map.put("Home", MainMenu.class);
        map.put("UserRegister-Email", UserRegisterEmail.class);
        map.put("UserRegister-Name",  UserRegisterName.class);
        map.put("UserRegister-Password", UserRegisterPassword.class);
        map.put("UserVault", UserVault.class);
        map.put("UserVault-AddAccount-Name",  UserVaultAddAccountName.class);
        map.put("UserVault-AddAccount-Password", UserVaultAddAccountPassword.class);
        map.put("UserVault-AddAccount-Username", UserVaultAddAccountUsername.class);
        map.put("UserVault-Erase", UserVaultErase.class);
        map.put("UserVault-ListAccounts", UserVaultListAccounts.class);
        map.put("UserVault-Settings", UserVaultSettings.class);
        return Collections.unmodifiableMap(map);
    }

    public Menu(String identifier) {
        this.identifier = identifier;
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

                if (option.hasAction()) {
                    option.getAction().run();
                }

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
    	return identifierClassMap.get(identifier);
    }


    public class MenuOption {
        private String title;

        private String nextMenuIdentifier;
        private Action doAction;

        public MenuOption(String title, String nextMenuIdentifier) {
            this.title = title;
            this.nextMenuIdentifier = nextMenuIdentifier;
        }

        public MenuOption(String title, Action action) {
            this.title = title;
            this.doAction = action;
        }

        public String getTitle() {
            return this.title;
        }

        public String getNextMenuIdentifier() {
            return this.nextMenuIdentifier;
        }

        public Action getAction() {
            return this.doAction;
        }

        public boolean hasNextMenu() {
            return this.nextMenuIdentifier != null;
        }

        public boolean hasAction() {
            return this.doAction != null;
        }
    }
}

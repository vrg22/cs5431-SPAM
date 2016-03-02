import java.util.*;

public class ClientUser extends Client {

    private Map<String, Menu> cachedMenus;
    private Menu currentMenu;

    public ClientUser() {
        cachedMenus = new HashMap<>();

        Menu mainMenu = getMenuWithName("Home");
        currentMenu = mainMenu;
    }

    public void run() {
        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.print(currentMenu);

            String input = in.nextLine();
            System.out.println();

            // Handle user input
            Menu newMenu = currentMenu.handleInput(input);
            currentMenu = newMenu != null ? newMenu : currentMenu;
        }
    }

    private Menu getMenuWithName(String name) {
        if (cachedMenus.containsKey(name)) {
            return cachedMenus.get(name);
        } else if (name.equals("Home")) {
            cachedMenus.put(name, new MainMenu());
            return getMenuWithName(name);
        } else if (name.equals("Register")) {
            cachedMenus.put(name, new RegisterMenu());
            return getMenuWithName(name);
        }

        return null;
    }

    public static void main(String[] args) {
        Client.printWelcome();

        ClientUser client = new ClientUser();
        client.run();
    }

    public abstract class Menu {
        protected String name;
        protected ArrayList<MenuOption> options;
        protected String prompt;

        public String getName() {
            return this.name;
        }

        /**
         * Process user's input
         * @return next menu to display (or null to remain on same menu)
         */
        public abstract Menu handleInput(String input);

        public String toString() {
            String out = this.name + ":\n";

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

                if (prompt != null) out += "\n";
            }
            if (prompt != null) {
                out += prompt;
            }

            return out;
        }
    }

    public class MainMenu extends Menu {
        public MainMenu() {
            MenuOption register = new MenuOption("Register for SPAM",
                getMenuWithName("Register"));
            MenuOption login = new MenuOption("Log in", new Action());

            ArrayList<MenuOption> options = new ArrayList<>();
            options.add(register);
            options.add(login);

            this.name = "Home";
            this.options = options;
            this.prompt = "Please selection an option (1 or 2): ";
        }

        public Menu handleInput(String input) {
            try {
                int i = Integer.parseInt(input);
                if (i >= 1 && i <= this.options.size()) {
                    MenuOption option = this.options.get(i - 1);

                    if (option.getNextMenu() != null) {
                        return option.getNextMenu();
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    public class RegisterMenu extends Menu {
        public RegisterMenu() {
            this.name = "Register";
            this.prompt = "Please enter your full name: ";
        }

        public Menu handleInput(String input) {
            return null;
        }
    }

    public class MenuOption {
        private String title;

        private Menu goToMenu;
        private Action doAction;

        public MenuOption(String title, Menu menu) {
            this.title = title;
            this.goToMenu = menu;
        }

        public MenuOption(String title, Action action) {
            this.title = title;
            this.doAction = action;
        }

        public String getTitle() {
            return this.title;
        }

        public Menu getNextMenu() {
            return this.goToMenu;
        }

        public Action getAction() {
            return this.doAction;
        }
    }

    public class Action {

    }
}

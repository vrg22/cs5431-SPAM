import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    // Set to false to fail (relatively) gracefully when features are not implemented
    private static final boolean DEV_MODE = true;

    private Map<String, String> loginErrorMessages;
    private Map<String, String> registerErrorMessages;

    private PasswordGenerator passwordGenerator;
    private static final int PASSWORD_LENGTH = 12;

    private boolean isLoggedIn = false; // TODO: this is just a placeholder for testing
    private int userId; // TODO: this is just a placeholder for testing

    public ClientController(ServerController server) {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        staticFileLocation("/public");

        // Initialize instance variables
        Gson gson = new Gson();
        populateErrorMessages();

        // Select algorithm for password generator
        passwordGenerator = new ComplexPasswordGenerator();

        // If already logged in, redirect to /users/:userid
        before("/", (request, response) -> {
            if (isLoggedIn) response.redirect("/users/" + userId);
        });

        // Show "Home (Logged out)" page
        get("/", (request, response) -> {
            return render("home.hbs", null);
        });

        // Check for valid server controller
        if (!DEV_MODE) {
            before("/*", (request, response) -> {
                String[] splat = request.splat();
                boolean isRoot = splat.length == 0;
                boolean isNoserver = splat.length == 1 && splat[0].equals("noserver");
                if (server == null && !isNoserver && !isRoot) {
                    response.redirect("/noserver");
                }
            });
        }

        // HTML: Show "Server not yet implemented" page
        get("/noserver", (request, response) -> render("noserver.hbs", null));

        // If already logged in, redirect to /users/:userid
        before("/login", (request, response) -> {
            if (isLoggedIn) response.redirect("/users/" + userId);
        });

        // HTML: Show "Login" page
        get("/login", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String errorCode = request.queryParams("error");
            if (loginErrorMessages.containsKey(errorCode)) {
                attributes.put("error", loginErrorMessages.get(errorCode));
            }
            return render("login.hbs", attributes);
        });

        // Log in user
        post("/login", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            int loginResult = server.login(email, password); // user ID or -1

            if (loginResult != -1) {
                // Log in user
                isLoggedIn = true;
                userId = loginResult;

                response.redirect("/");
            } else {
                // Incorrect email and/or password
                response.redirect("/login?error=1");
            }

            return "";
        });

        // HTML: Show "Register new user" page
        get("/register", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            String errorCode = request.queryParams("error");
            if (registerErrorMessages.containsKey(errorCode)) {
                attributes.put("error", registerErrorMessages.get(errorCode));
            }
            return render("register.hbs", attributes);
        });

        // Register new user
        post("/register", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            boolean isEmailValid = isEmailValid(email),
                    isPasswordValid = isPasswordValid(password);

            if (isEmailValid && isPasswordValid) {
                User newUser = server.registerNewUser(email, password);
                if (newUser == null) {
                    response.redirect("/"); // Unknown server error
                    return "";
                }

                // TODO: Log in new user
                isLoggedIn = true;
                userId = newUser.getID();

                response.redirect("/");
            } else if (isPasswordValid) {
                // Invalid email
                response.redirect("/register?error=1");
            } else {
                // Invalid password
                response.redirect("/register?error=2");
            }

            return "";
        });

        // If not logged in, redirect to /
        // If logged in as different user, redirect to /users/<logged-in user id>
        before("/users/:userid", (request, response) -> {
            if (!isLoggedIn) {
                response.redirect("/");
            } else if (!("" + userId).equals(request.params("userid"))) {
                response.redirect("/users/" + userId);
            }
        });

        // HTML: Show "User Vault" page
        get("/users/:userid", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userid", request.params("userid"));
            return render("vault.hbs", attributes);
        });

        // Obliterate entire user account
        delete("/users/:userid", (request, response) -> {
            try {
                int userId = Integer.parseInt(request.params("userid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return false;
            }

            boolean result = server.obliterateUser(userId);
            return result;
        });

        // If not logged in, redirect to /
        // If logged in as different user, redirect to /users/<logged-in user id>
        before("/users/:userid/*", (request, response) -> {
            if (!isLoggedIn) {
                response.redirect("/");
            } else if (!("" + userId).equals(request.params("userid"))) {
                response.redirect("/users/" + userId);
            }
        });

        // HTML: Show "View/edit my stored accounts" page
        // JSON: Get list of stored accounts for user
        get("/users/:userid/accounts", (request, response) -> {
            int userId;
            try {
                userId = Integer.parseInt(request.params("userid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return false;
            }

            if (request.headers("Accept").contains("text/html")) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userid", userId);

                String accounts = sendGet(request.url(), "text/json");
                attributes.put("accounts", accounts);

                return render("showaccounts.hbs", attributes);
            } else {
                // Default content type: JSON

                Account.Header[] accounts = server.getAccountsForUser(userId);
                return gson.toJson(accounts);
            }
        });

        // HTML: Show "Store new account" page
        get("/users/:userid/accounts/create", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userid", request.params("userid"));
            return render("addnew.hbs", attributes);
        });

        // Store new account
        post("/users/:userid/accounts/create", (request, response) -> {
            String name = request.queryParams("name");
            String username = request.queryParams("username");
            String password = request.queryParams("password");

            //TODO: DO any conditions need to be checked here? <3rd-party account>

            if (name != null && password != null) { //TODO: Can password be null? How to reflect empty?
                Account newAcct = server.storeNewAccountForUser(userId, name, username, password);
                if (newAcct == null) {
                    response.redirect("/"); // Unknown server error
                    return "";
                }

                response.redirect("/users/" + request.params("userid") + "/accounts");
            } else if (name == null) {
                // Invalid name
                response.redirect("/addnew?error=1");
            } else if (password == null) {
                // Invalid password
                response.redirect("/addnew?error=2");
            }

            return "";
        });

        // HTML: Show "View/edit an account" page
        // JSON: Get details for an account
        get("/users/:userid/accounts/:accountid", (request, response) -> {
            int userId, accountId;
            try {
                userId = Integer.parseInt(request.params("userid"));
                accountId = Integer.parseInt(request.params("accountid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return "";
            }

            if (request.headers("Accept").contains("text/html")) {
                if (!server.isAccountForUser(accountId, userId)) {
                    // Bad request
                    response.status(400);
                    return "";
                }

                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userid", userId);
                attributes.put("accountid", accountId);

                String details = sendGet(request.url(), "text/json");
                attributes.put("account", details);

                return render("showaccount.hbs", attributes);
            } else {
                // Default content type: JSON

                if (!server.isAccountForUser(accountId, userId)) {
                    // Bad request
                    response.status(400);
                    return gson.toJson("");
                }

                Account account = server.getDetailsForAccount(userId, accountId);
                if (account == null) return gson.toJson("");

                return gson.toJson(account);
            }
        });

        // Update a stored account
        put("/users/:userid/accounts/:accountid", (request, response) -> {
            int userId, accountId;
            try {
                userId = Integer.parseInt(request.params("userid"));
                accountId = Integer.parseInt(request.params("accountid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return false;
            }

            if (!server.isAccountForUser(accountId, userId)) {
                // Bad request
                response.status(400);
                return false;
            }

            // TODO: implement- figure out where the account params are in the request
            // Account account = ...;
            // return server.updateAccount(account);

            return "Not yet implemented";
        });

        // Delete a stored account
        delete("/users/:userid/accounts/:accountid", (request, response) -> {
            int userId, accountId;
            try {
                userId = Integer.parseInt(request.params("userid"));
                accountId = Integer.parseInt(request.params("accountid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return false;
            }

            if (!server.isAccountForUser(accountId, userId)) {
                // Bad request
                response.status(400);
                return false;
            }

            boolean result = server.deleteAccount(accountId, userId);
            return result;
        });

        // HTML: Show "Confirm obliterate SPAM account" page
        get("/users/:userid/delete", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userid", request.params("userid"));
            return render("confirmdeletespam.hbs", attributes);
        });

        // Get random password
        get("/password", (request, response) -> {
            response.type("text/plain");
            return passwordGenerator.next(PASSWORD_LENGTH);
        });
    }

    // Return HTML for a Handlebars template
    private String render(String template, Map<String, Object> attributes) {
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(attributes, template));
    }

    // Send GET request to specified URL
    // with Accept header set to specified content type.
    private String sendGet(String url, String accept) {
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();

            con.setRequestMethod("GET");
            if (accept != null) con.setRequestProperty("Accept", accept);

            int responseCode = con.getResponseCode();

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // return response
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Initialize error message maps
    private void populateErrorMessages() {
        loginErrorMessages = new HashMap<>();
        loginErrorMessages.put("1", "Incorrect email and/or password.");

        registerErrorMessages = new HashMap<>();
        registerErrorMessages.put("1", "Invalid email address.");
        registerErrorMessages.put("2", "Invalid master password.");
    }

    // Check that `email` is a valid email address (e.g., some@email.com)
    private static boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
        return email != null && emailPattern.matcher(email).matches();
    }

    // Check that `password` fits requirements for master passwords
    // TODO: Decide/implement requirements
    private static boolean isPasswordValid(String password) {
        return password != null && password.length() > 0;
    }

}

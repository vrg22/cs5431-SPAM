import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    private Map<String, String> loginErrorMessages;
    private Map<String, String> registerErrorMessages;

    private boolean isLoggedIn = false; // TODO: this is just a placeholder for testing
    private String userId; // TODO: this is just a placeholder for testing

    public ClientController(StoreAndRetrieveUnit sru) {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        staticFileLocation("/public");

        Gson gson = new Gson();

        populateErrorMessages();

        // If already logged in, redirect to /users/:userid
        before("/", (request, response) -> {
            if (isLoggedIn) response.redirect("/users/" + userId);
        });

        // Show "Home (Logged out)" page
        get("/", (request, response) -> {
            return render("home.hbs", null);
        });

        // If already logged in, redirect to /users/:userid
        before("/login", (request, response) -> {
            if (isLoggedIn) response.redirect("/users/" + userId);
        });

        // Show "Login" page (HTML)
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

            // TODO: replace with actual check for correct username/password
            boolean isCorrect = email != null && email.length() > 0
                    && password != null && password.length() > 0;

            if (isCorrect) {
                // Correct email-password combination
                // TODO: login user here
                isLoggedIn = true;
                userId = "sldfkjslk";
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
                // TODO: register new user (sru.register_new_user(...))
                // TODO: log in the new user (sru.login_user(...))
                isLoggedIn = true;
                userId = "sdlfkjl";
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
            } else if (!userId.equals(request.params("userid"))) {
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
            // TODO: implement (sru.obliterate(...))
            return "Not yet implemented";
        });

        // If not logged in, redirect to /
        // If logged in as different user, redirect to /users/<logged-in user id>
        before("/users/:userid/*", (request, response) -> {
            if (!isLoggedIn) {
                response.redirect("/");
            } else if (!userId.equals(request.params("userid"))) {
                response.redirect("/users/" + userId);
            }
        });

        // HTML: Show "View/edit my stored accounts" page
        // JSON: Get list of stored accounts for user
        get("/users/:userid/accounts", (request, response) -> {
            if (request.headers("Accept").contains("text/html")) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userid", request.params("userid"));

                String accounts = sendGet(request.url(), "text/json");
                attributes.put("accounts", accounts);

                return render("showaccounts.hbs", attributes);
            } else {
                // Default content type: JSON
                // TODO: implement (sru.list_items(...))
                return gson.toJson("Not yet implemented");
            }
        });

        // Store a new account for a user
        post("/users/:userid/accounts", (request, response) -> {
            // TODO: implement (sru.???)
            return "Not yet implemented";
        });

        // HTML: Show "Store new account" page
        get("/users/:userid/accounts/create", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userid", request.params("userid"));
            return render("addnew.hbs", attributes);
        });

        // HTML: Show "View/edit an account" page
        // JSON: Get details for an account
        get("/users/:userid/accounts/:accountid", (request, response) -> {
            if (request.headers("Accept").contains("text/html")) {
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userid", request.params("userid"));
                attributes.put("accountid", request.params("accountid"));

                String details = sendGet(request.url(), "text/json");
                attributes.put("account", details);

                return render("showaccount.hbs", attributes);
            } else {
                // Default content type: JSON
                // TODO: implement (sru.retrieve_userID(...))
                return gson.toJson("Not yet implemented");
            }
        });

        // Update a stored account
        put("/users/:userid/accounts/:accountid", (request, response) -> {
            // TODO: implement (sru.edit_userID(...))
            return "Not yet implemented";
        });

        // Delete a stored account
        delete("/users/:userid/accounts/:accountid", (request, response) -> {
            // TODO: implement (sru.delete_userID(...))
            return "Not yet implemented";
        });

        // HTML: Show "Confirm obliterate SPAM account" page
        get("/users/:userid/delete", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("userid", request.params("userid"));
            return render("confirmdeletespam.hbs", attributes);
        });
    }

    // Return HTML for a Handlebars template
    private String render(String template, Map<String, Object> attributes) {
        return new HandlebarsTemplateEngine()
            .render(new ModelAndView(attributes, template));
    }

    // Send GET request
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

    // Verify that `email` is a valid email address
    private static boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
        return email != null && emailPattern.matcher(email).matches();
    }

    // TODO: check to make sure password fits certain requirements (TBD)
    private static boolean isPasswordValid(String password) {
        return password != null && password.length() > 0;
    }

}

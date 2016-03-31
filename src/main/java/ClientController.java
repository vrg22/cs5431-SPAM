import java.util.*;
import java.util.regex.*;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    private Map<String, String> loginErrorMessages;
    private Map<String, String> registerErrorMessages;

    public ClientController(StoreAndRetrieveUnit sru) {
        port(Integer.valueOf(System.getenv("PORT")));
        staticFileLocation("/public");

        Gson gson = new Gson();

        populateErrorMessages();

        // Redirect to "Login" page if not logged in
        before("/", (request, response) -> {
            boolean isLoggedIn = false; // TODO: set this
            if (!isLoggedIn) {
                response.redirect("/login");
            }
        });

        // Show "User Vault - Home" page
        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "You're logged in!");

            return new ModelAndView(attributes, "hello.hbs");
        }, new HandlebarsTemplateEngine());

        // Show "Login" page
        get("/login", (request, response) -> {
            return new ModelAndView(null, "login.hbs");
        }, new HandlebarsTemplateEngine());
        get("/login/:error", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String errorCode = request.params("error");
            if (loginErrorMessages.containsKey(errorCode)) {
                attributes.put("error", loginErrorMessages.get(errorCode));
            }

            return new ModelAndView(attributes, "login.hbs");
        }, new HandlebarsTemplateEngine());

        // Attempt to log in
        post("/login", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            // TODO: replace with actual check for correct username/password
            boolean isCorrect = email != null && email.length() > 0
                    && password != null && password.length() > 0;

            if (isCorrect) {
                // Correct email-password combination
                // TODO: login user here
                response.redirect("/");
            } else {
                // Incorrect email and/or password
                response.redirect("/login/1");
            }

            return "";
        });

        // Show "Register new user" page
        get("/register", (request, response) -> {
            return new ModelAndView(null, "register.hbs");
        }, new HandlebarsTemplateEngine());
        get("/register/:error", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();

            String errorCode = request.params("error");
            if (registerErrorMessages.containsKey(errorCode)) {
                attributes.put("error", registerErrorMessages.get(errorCode));
            }

            return new ModelAndView(attributes, "register.hbs");
        }, new HandlebarsTemplateEngine());

        // Register new user
        post("/register", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            boolean isEmailValid = isEmailValid(email),
                    isPasswordValid = isPasswordValid(password);

            if (isEmailValid && isPasswordValid) {
                // TODO: create new user and log in the new user

                response.redirect("/");
            } else if (isPasswordValid) {
                // Invalid email
                response.redirect("/register/1");
            } else {
                // Invalid password
                response.redirect("/register/2");
            }

            return "";
        });

        // Get list of stored accounts for a user
        // TODO: implement
        get("/accounts/:userid", (request, response) -> {
            String userid = request.params("userid");

            response.status(501);
            return "Not yet implemented";
        }, gson::toJson);

        // Get details for a single stored account
        // TODO: implement
        get("/accounts/:userid/:accountid", (request, response) -> {
            String userid = request.params("userid");
            String accountid = request.params("accountid");

            response.status(501);
            return "Not yet implemented";
        }, gson::toJson);

        // Store a new account
        // TODO: implement
        post("/accounts/:userid", (request, response) -> {
            String userid = request.params("userid");

            response.status(501);
            return "Not yet implemented";
        });

        // Update a stored account
        // TODO: implement
        put("/accounts/:userid/:accountid", (request, response) -> {
            String userid = request.params("userid");
            String accountid = request.params("accountid");

            response.status(501);
            return "Not yet implemented";
        });

        // Delete a stored account
        // TODO: implement
        delete("/accounts/:userid/:accountid", (request, response) -> {
            String userid = request.params("userid");
            String accountid = request.params("accountid");

            response.status(501);
            return "Not yet implemented";
        });

        // Obliterate entire user account
        // TODO: implement
        delete("/accounts/:userid", (request, response) -> {
            String userid = request.params("userid");

            response.status(501);
            return "Not yet implemented";
        });


    }

    private void populateErrorMessages() {
        loginErrorMessages = new HashMap<>();
        loginErrorMessages.put("1", "Incorrect email and/or password.");

        registerErrorMessages = new HashMap<>();
        registerErrorMessages.put("1", "Invalid email address.");
        registerErrorMessages.put("2", "Invalid master password.");
    }

    private boolean isEmailValid(String email) {
        Pattern emailPattern = Pattern.compile("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");
        return email != null && emailPattern.matcher(email).matches();
    }

    // TODO: check to make sure password fits certain requirements (TBD)
    private boolean isPasswordValid(String password) {
        return password != null && password.length() > 0;
    }

}

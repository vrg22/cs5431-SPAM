import java.util.*;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    private Map<String, String> loginErrorMessages;

    public ClientController(StoreAndRetrieveUnit sru) {
        port(Integer.valueOf(System.getenv("PORT")));

        Gson gson = new Gson();

        populateLoginErrorMessages();

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
                response.redirect("/login/403");
            }

            return "";
        });

        // Show "Register new user" page
        get("/register", (request, response) -> {
            return new ModelAndView(null, "register.hbs");
        }, new HandlebarsTemplateEngine());

        // Register new user
        post("/register", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            // TODO: replace with check for valid email address
            //      (and in the future, that the password fits some recipe)
            boolean isValid = email != null && email.length() > 0
                    && password != null && password.length() > 0;

            if (isValid) {
                // TODO: create new user and log in the new user

                response.redirect("/");
            } else {
                // Invalid email or password
                // TODO: display appropriate error message
                response.redirect("/register");
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

    private void populateLoginErrorMessages() {
        loginErrorMessages = new HashMap<>();
        loginErrorMessages.put("403", "Incorrect email and/or password.");
    }

}

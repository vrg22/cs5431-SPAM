import java.util.*;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    public ClientController(ClientService service) {
        port(Integer.valueOf(System.getenv("PORT")));

        // Redirect to "Login" page if not logged in
        before("/", (request, response) -> {
            if (!service.hasUser()) {
                response.redirect("/login");
            }
        });

        // Show "User Vault - Home" page
        get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "hello.hbs");
        }, new HandlebarsTemplateEngine());

        // Show "Login" page
        get("/login", (request, response) -> {
            return new ModelAndView(null, "login.hbs");
        }, new HandlebarsTemplateEngine());

        // Attempt to log in
        post("/login", (request, response) -> {
            String email = request.queryParams("email");
            String password = request.queryParams("password");

            if (email != null && email.length() > 0
                    && password != null && password.length() > 0) {
                // Valid login credentials
                service.setUser();
                response.redirect("/");
            } else {
                // Invalid login credentials
                response.redirect("/login");
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

            if (email != null && email.length() > 0
                    && password != null && password.length() > 0) {
                service.createUser(request.queryParams("email"),
                    request.queryParams("password"));
                service.setUser();
                response.redirect("/");
            } else {
                // Invalid email or password
                response.redirect("/register");
            }

            return "";
        });
    }

}

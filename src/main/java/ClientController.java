import java.util.*;
import java.util.regex.*;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class ClientController {

    public ClientController(ServerController server) {
        if (System.getenv("PORT") != null) {
            port(Integer.valueOf(System.getenv("PORT")));
        }

        staticFileLocation("/public");

        // Initialize instance variables
        Gson gson = new Gson();
        XMLStorageController store = new XMLStorageController();
        FileInputStream passwordStream;
        try {
            passwordStream = new FileInputStream("users.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        PasswordStorageFile passwordFile = store.readPasswordsFile(passwordStream);
        store.writeFileToDisk(passwordFile);

        // Log in user
        post("/login", (request, response) -> {
            String email = request.queryParams("email");

            // TODO: construct LoginResponse
            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry != null) {
                int id = entry.getUserId();
                String vault = new String(Files.readAllBytes(Paths.get(id + ".xml")));;
                String saltedHash = entry.getMaster();
                byte[] salt = entry.getSalt();
                String iv = new String(entry.getIV());
                LoginResponse body = new LoginResponse(id, vault, saltedHash, salt, iv);
                return gson.toJson(body);
            }

            return "";
        });

        // Register new user
        post("/register", (request, response) -> {
            System.out.println("Registering");
            String email = request.queryParams("email");
            String salt = request.queryParams("salt");
            String saltedHash = request.queryParams("saltedHash");
            String vault = request.queryParams("vault");

            User newUser = server.registerNewUser(email, salt, saltedHash,
                vault, request.ip());
            if (newUser == null) {
                RegisterResponse body = new RegisterResponse(false);
                response.body(gson.toJson(body));
                return "";
            }

            RegisterResponse body = new RegisterResponse(true);

            return gson.toJson(body);
        });

        // Obliterate entire user account
        delete("/users/:userid", (request, response) -> {
            int userId = -1;
            try {
                userId = Integer.parseInt(request.params("userid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                return false;
            }

            boolean result = server.obliterateUser(userId, request.ip());

            RegisterResponse body = new RegisterResponse(result);

            return gson.toJson(body);
        });
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

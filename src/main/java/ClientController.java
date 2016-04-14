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

    private PasswordStorageFile passwordFile;

    public ClientController(ServerController server) {
        if (System.getenv("PORT") != null) {
            port(Integer.parseInt(System.getenv("PORT")));
        }

        staticFileLocation("/public");
        secure("keystore.jks", "cs5431spamisthebest", null, null);

        // Initialize instance variables
        Gson gson = new Gson();
        XMLStorageController store = new XMLStorageController(server.getPasswordsFilename());
        try {
            FileInputStream passwordStream = null;
            try {
                passwordStream = new FileInputStream(store.getPasswordsFilename());
                passwordFile = store.readPasswordsFile(passwordStream);
                store.writeFileToDisk(passwordFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } finally {
                if (passwordStream != null) passwordStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // Log in user
        post("/login", (request, response) -> {
            String email = request.queryParams("email");

            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry != null) {
                int id = entry.getUserId();
                String vault = new String(Files.readAllBytes(Paths.get(
                    store.getFilenameForUser(id))));
                String saltedHash = entry.getMaster();
                byte[] salt = entry.getSalt();
                byte[] iv = entry.getIV();
                LoginResponse body = new LoginResponse(id, vault, saltedHash,
                    salt, iv);
                return gson.toJson(body);
            }

            // User with specified email does not exist
            return "";
        });

        // Register new user
        post("/register", (request, response) -> {
            String email = request.queryParams("email");
            String salt = request.queryParams("salt");
            String saltedHash = request.queryParams("saltedHash");
            String vault = request.queryParams("vault");
            String iv = request.queryParams("iv");

            User newUser = server.registerNewUser(email, salt, saltedHash,
                vault, iv, request.ip(), passwordFile);
            if (newUser == null) {
                RegisterResponse body = new RegisterResponse(false);
                response.body(gson.toJson(body));
                return "";
            }

            RegisterResponse body = new RegisterResponse(true);

            return gson.toJson(body);
        });

        // Update user vault
        post("/users/:userid/save", (request, response) -> {
            int userId = -1;
            try {
                userId = Integer.parseInt(request.params("userid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                SaveResponse body = new SaveResponse(false);
                return gson.toJson(body);
            }

            String vault = request.queryParams("vault");
            String iv = request.queryParams("iv");

            server.updateUserVault(userId, vault, iv, passwordFile);

            SaveResponse body = new SaveResponse(true);
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

            boolean result = server.obliterateUser(userId, request.ip(),
                passwordFile);

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

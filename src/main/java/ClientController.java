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

        // Send client salt for user with specified username
        post("/salt", (request, response) -> {
            String email = request.queryParams("email");

            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry == null) return "";

            byte[] salt = entry.getSalt();
            SaltResponse body = new SaltResponse(salt);
            return gson.toJson(body);
        });

        // If correct master password, send client user's ID,
        // IV, and encrypted vault
        post("/auth", (request, response) -> {
            String email = request.queryParams("email");
            String saltedHash = request.queryParams("master");

            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry == null) return "";

            String correctSaltedHash = entry.getMaster();
            if (!saltedHash.equals(correctSaltedHash)) return "";

            int id = entry.getUserId();
            String vault = new String(Files.readAllBytes(Paths.get(
                store.getFilenameForUser(id))));
            byte[] iv = entry.getIV();
            AuthResponse body = new AuthResponse(id, vault, iv);
            return gson.toJson(body);
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

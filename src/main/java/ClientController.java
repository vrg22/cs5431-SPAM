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
        port(4567);

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

        get("/", (request, response) -> {
            return "";
        });

        // Send salt for user with specified username
        post("/salt", (request, response) -> {
            String email = request.queryParams("email");
            String type = request.queryParams("type");
            
            PasswordStorageEntry entry = passwordFile.getWithUsername(type, email);
            if (entry == null) {
                server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
                    + "was made to get salt for non-existent " + type + " with "
                    + "username " + email + ".");
                return "";
            }

            byte[] salt = entry.getSalt();
            SaltResponse body = new SaltResponse(salt);

            return gson.toJson(body);
        });

        // If correct master password, send client's ID,
        // IV, and encrypted vault (null if admin)
        post("/auth", (request, response) -> {
            String email = request.queryParams("email");
            String type = request.queryParams("type");
            String saltedHash = request.queryParams("master");

            PasswordStorageEntry entry = passwordFile.getWithUsername(type, email);
            if (entry == null) {
                server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
                    + "was made to authenticate as non-existent " + type + " with "
                    + "username " + email + ".");
                return "";
            }

            String correctSaltedHash = entry.getMaster();
            if (!saltedHash.equals(correctSaltedHash)) {
                server.getLogger().warning("[IP=" + request.ip() + "] "
                    + "Incorrect password while attempting to authenticate "
                    + "as " + type + " with username " + email + ".");
                return "";
            }

            AuthResponse body;
            int id = entry.getId();
	        byte[] iv = entry.getIV();
	        String vault = null;
	        
            if (type.equals("user")) {
	            vault = new String(Files.readAllBytes(Paths.get(
	                store.getFilenameForUser(id))));
            }
            
            body = new AuthResponse(id, vault, iv);

            return gson.toJson(body);
        });

        // Register new client (user or admin)
        post("/register", (request, response) -> {
            String email = request.queryParams("email");
            String type = request.queryParams("type");
            String salt = request.queryParams("salt");
            String saltedHash = request.queryParams("saltedHash");
            String vault = request.queryParams("vault"); //CHECK: null for admins
            String iv = request.queryParams("iv");

            if (!isEmailValid(email)) {
                // Invalid email
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            if (type.equals("user")) {
            	User newUser = server.registerNewUser(email, salt, saltedHash,
            			vault, iv, request.ip(), passwordFile);
	            if (newUser == null) {
	                // User already exists, or other problem creating the user
	                RegisterResponse body = new RegisterResponse(false);
	                return gson.toJson(body);
	            }
	
	            RegisterResponse body = new RegisterResponse(true);
	            return gson.toJson(body);
            }
            else { //CHECK: Assume type=admin?
            	Admin newAdmin = server.registerNewAdmin(email, salt, 
            			saltedHash, iv, request.ip(), passwordFile);
            	
            	if (newAdmin == null) {
	                // Admin already exists, or other problem creating the admin
	                RegisterResponse body = new RegisterResponse(false);
	                return gson.toJson(body);
	            }
	
	            RegisterResponse body = new RegisterResponse(true);
	            return gson.toJson(body);
            }
            
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
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            boolean result = server.obliterateUser(userId, request.ip(),
                passwordFile);

            RegisterResponse body = new RegisterResponse(result);
            return gson.toJson(body);
        });
        
        // - ADMIN-SPECIFIC OPERATIONS - 
        // Obliterate entire admin account
        delete("/admins/:adminid", (request, response) -> {
            int adminId = -1;
            try {
                adminId = Integer.parseInt(request.params("adminid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            boolean result = server.obliterateAdmin(adminId, request.ip(),
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

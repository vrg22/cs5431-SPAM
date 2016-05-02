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

    // <user id> -> [<session 1 auth key>, <session 2 auth key>, ...]
    private HashMap<Integer, ArrayList<AuthenticationKey>> authKeys;

    // <user id> -> # consecutive failed login attempts
    private HashMap<Integer, Integer> failedAuthAttempts;

    public ClientController(ServerController server) {
        port(4567);

        staticFileLocation("/public");
        secure("keystore.jks", "cs5431spamisthebest", null, null);

        // Initialize instance variables
        Gson gson = new Gson();
        authKeys = new HashMap<>();
        failedAuthAttempts = new HashMap<>();
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

        // Send client salt for user with specified username
        post("/salt", (request, response) -> {
            String email = request.queryParams("email");

            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry == null) {
                server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
                    + "was made to get salt for non-existent user with "
                    + "username " + email + ".");
                return "";
            }

            byte[] salt = entry.getSalt();
            SaltResponse body = new SaltResponse(salt);

            return gson.toJson(body);
        });

        // If correct recovery password, send client's encrypted pass
        // recovery IV
        
        post("/recover", (request, response) -> {
          String email = request.queryParams("email");
          String saltedRecovery = request.queryParams("recovery");

          PasswordStorageEntry entry = passwordFile.getWithUsername(email);
          if (entry == null) {
            server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
              + "was made to recover a non-existent user with "
              + "username " + email + ".");
            return "";
          }

          String correctSaltedRecovery = entry.getRecovery();
          if (!saltedRecovery.equals(correctSaltedRecovery)) {
            server.getLogger().warning("[IP=" + request.ip() + "] "
              + "Incorrect recovery password while attempting to recover "
              + "as user with username " + email + ".");
          }

          String encPass = entry.getEncPass();
          byte[] reciv = entry.getRecIV();

          RecoResponse body = new RecoResponse(encPass, reciv);
          return gson.toJson(body);
        });

        // If correct master password, send client user's ID,
        // IV, and encrypted vault
        post("/auth", (request, response) -> {
            String email = request.queryParams("email");
            String saltedHash = request.queryParams("master");
            String initialAuthKey = request.queryParams("nextAuthKey");

            PasswordStorageEntry entry = passwordFile.getWithUsername(email);
            if (entry == null) {
                server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
                    + "was made to authenticate as non-existent user with "
                    + "username " + email + ".");
                return "";
            }

            String correctSaltedHash = entry.getMaster();
            if (!saltedHash.equals(correctSaltedHash)) {
                server.getLogger().warning("[IP=" + request.ip() + "] "
                    + "Incorrect password while attempting to authenticate "
                    + "as user with username " + email + ".");

                int id = entry.getUserId();
                int attempts = 0;
                if (failedAuthAttempts.containsKey(id)) {
                    attempts = failedAuthAttempts.get(id);
                }
                attempts++;
                failedAuthAttempts.put(id, attempts);
                rateLimit(attempts);

                return "";
            }

            int id = entry.getUserId();
            String vault = new String(Files.readAllBytes(Paths.get(
                store.getFilenameForUser(id))));
            byte[] iv = entry.getIV();
            addInitialAuthKeyForUser(id, initialAuthKey);

            failedAuthAttempts.put(id, 0); // Reset failed attempts counter

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
			String encPass = request.queryParams("encryptedPass");
			String reciv = request.queryParams("reciv");
			String recoveryHash = request.queryParams("recoveryHash");

            if (!isEmailValid(email)) {
                // Invalid email
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            User newUser = server.registerNewUser(email, salt, saltedHash,
                vault, iv, request.ip(), passwordFile, encPass, reciv, recoveryHash);
            if (newUser == null) {
                // User already exists, or other problem creating the user
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            RegisterResponse body = new RegisterResponse(true);
            return gson.toJson(body);
        });

        // Update user vault
        // *Authorization required
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
            String authKey = request.queryParams("authKey");
            String nextAuthKey = request.queryParams("nextAuthKey");

            if (isValidAuthKeyForUser(userId, authKey)) {
                updateAuthKeyForUser(userId, authKey, nextAuthKey);
                server.updateUserVault(userId, vault, iv, passwordFile);

                SaveResponse body = new SaveResponse(true);
                return gson.toJson(body);
            } else {
                // Invalid authentication key -> Authorization failed
                SaveResponse body = new SaveResponse(false);
                return gson.toJson(body);
            }
        });

        post("/users/:userid/resetpass", (request, response) -> {
            int userId = -1;
            try {
                userId = Integer.parseInt(request.params("userid"));
            } catch (NumberFormatException e) {
                // Bad request
                response.status(400);
                SaveResponse body = new SaveResponse(false);
                return gson.toJson(body);
            }

            String saltedHash = request.queryParams("saltedHash");
            String authKey = request.queryParams("authKey");
            String nextAuthKey = request.queryParams("nextAuthKey");
			String encPass = request.queryParams("encryptedPass");
			String reciv = request.queryParams("reciv");

            if (isValidAuthKeyForUser(userId, authKey)) {
                updateAuthKeyForUser(userId, authKey, nextAuthKey);
                server.updateUser(userId, saltedHash, encPass, reciv, passwordFile);

                SaveResponse body = new SaveResponse(true);
                return gson.toJson(body);
            } else {
                // Invalid authentication key -> Authorization failed
				System.out.println("Authorization failed for resetpass");
                SaveResponse body = new SaveResponse(false);
                return gson.toJson(body);
            }
		});

        // Obliterate entire user account
        // *Authorization required
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

            String authKey = request.queryParams("authKey");
            String nextAuthKey = request.queryParams("nextAuthKey");

            if (isValidAuthKeyForUser(userId, authKey)) {
                updateAuthKeyForUser(userId, authKey, nextAuthKey);
                boolean result = server.obliterateUser(userId, request.ip(),
                    passwordFile);

                RegisterResponse body = new RegisterResponse(result);
                return gson.toJson(body);
            } else {
                // Invalid authentication key -> Authorization failed
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }
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

    private boolean isValidAuthKeyForUser(int userId, String key) {
        ArrayList<AuthenticationKey> userKeys = authKeys.get(userId);
		System.out.println("isValidAuthKeyForUser key:" +key);
        if (userKeys == null) {
			System.err.println("isValidAuthKeyForUser userKeys is null");
            return false;
        }

        for (int i = 0; i < userKeys.size(); i++) {
            AuthenticationKey aKey = userKeys.get(i);
            if (aKey.hasExpired()) {
                userKeys.remove(i);
                i--;
            } else if (aKey.matches(key)) {
                return true;
            }
        }

		System.err.println("isValidAuthKeyForUser is false");
        return false;
    }

    private void addInitialAuthKeyForUser(int userId, String key) {
        ArrayList<AuthenticationKey> userKeys = authKeys.get(userId);
        if (userKeys == null) {
            userKeys = new ArrayList<>();
            userKeys.add(new AuthenticationKey(key));
            authKeys.put(userId, userKeys);
        } else {
            userKeys.add(new AuthenticationKey(key));
        }
    }

    private void updateAuthKeyForUser(int userId, String oldKey, String newKey) {
        ArrayList<AuthenticationKey> userKeys = authKeys.get(userId);
        if (userKeys == null) return;

        for (int i = 0; i < userKeys.size(); i++) {
            AuthenticationKey aKey = userKeys.get(i);

            if (aKey.matches(oldKey)) {
                userKeys.set(i, new AuthenticationKey(newKey));
                return;
            }
        }
    }

    private void rateLimit(int numAttempts) {
        int delaySeconds = numAttempts * numAttempts;
        try {
            Thread.sleep(delaySeconds * 1000);
        } catch (InterruptedException e) {
        }
    }
}

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

        // If correct recovery password, send client's encrypted pass
        // and recovery IV
        post("/recover", (request, response) -> {
          String email = request.queryParams("email");
          String saltedRecovery = request.queryParams("recovery");
          String twoFactorCode = request.queryParams("twoFactorCode");

          PasswordStorageEntry entry = passwordFile.getWithUsername(email);
          if (entry == null) {
            server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
              + "was made to recover a non-existent user with "
              + "username " + email + ".");
            return "";
          }

          String correctSaltedRecovery = entry.getRecovery();
          if (!saltedRecovery.equals(correctSaltedRecovery) ||
                !isValidTwoFactorCodeForUser(email, twoFactorCode)) {
            server.getLogger().warning("[IP=" + request.ip() + "] "
              + "Incorrect recovery credentials while attempting to recover "
              + "as user with username " + email + ".");
			return "";
          }

          String encPass = entry.getEncPass();
          byte[] reciv = entry.getRecIV();

          RecoResponse body = new RecoResponse(encPass, reciv);
          return gson.toJson(body);
        });

        // If correct master password & two-factor code, send client user's ID,
        // IV, and encrypted vault (null if admin)
        post("/auth", (request, response) -> {
            String email = request.queryParams("email");
            String type = request.queryParams("type");
            String saltedHash = request.queryParams("master");
            String initialAuthKey = request.queryParams("nextAuthKey");
            String twoFactorCode = request.queryParams("twoFactorCode");

            PasswordStorageEntry entry = passwordFile.getWithUsername(type, email);
            if (entry == null) {
                server.getLogger().warning("[IP=" + request.ip() + "] Attempt "
                    + "was made to authenticate as non-existent " + type + " with "
                    + "username " + email + ".");
                return "";
            }

            String correctSaltedHash = entry.getMaster();
            if (!saltedHash.equals(correctSaltedHash) ||
                    !isValidTwoFactorCodeForUser(entry.getUserId(), twoFactorCode)) {
                server.getLogger().warning("[IP=" + request.ip() + "] "
                    + "Incorrect credentials while attempting to authenticate "
                    + "as " + type + " with username " + email + ".");

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

            AuthResponse body;
            int id = entry.getId();
            addInitialAuthKeyForUser(id, initialAuthKey);
            failedAuthAttempts.put(id, 0); // Reset faield attempts counter

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
			String encPass = request.queryParams("encryptedPass");
			String reciv = request.queryParams("reciv");
			String recoveryHash = request.queryParams("recoveryHash");
            String twoFactorSecret = request.queryParams("twoFactorSecret");

            if (!isEmailValid(email)) {
                // Invalid email
                RegisterResponse body = new RegisterResponse(false);
                return gson.toJson(body);
            }

            if (type.equals("user")) {
            	User newUser = server.registerNewUser(email, salt, saltedHash,
            			vault, iv, request.ip(), passwordFile, encPass, reciv,
                        recoveryHash, twoFactorSecret);
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
            			saltedHash, iv, request.ip(), passwordFile, encPass,
                        reciv, recoveryHash, twoFactorSecret);

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

        // Reset master password for user
        // *Authorization required
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

    private boolean isValidTwoFactorCodeForUser(int userId, String code) {
        PasswordStorageEntry entry = passwordFile.getWithUserId("" + userId);
        return isValidTwoFactorCodeForUser(entry, code);
    }

    private boolean isValidTwoFactorCodeForUser(String email, String code) {
        PasswordStorageEntry entry = passwordFile.getWithUsername(email);
        return isValidTwoFactorCodeForUser(entry, code);
    }

    private boolean isValidTwoFactorCodeForUser(PasswordStorageEntry entry, String code) {
        long codeLong;
        try {
            codeLong = Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }

        String secret = entry.getTwoFactorSecret();
        return CryptoServiceProvider.verifyTwoFactorCodeWithSecret(secret, codeLong);
    }

    private void rateLimit(int numAttempts) {
        int delaySeconds = numAttempts * numAttempts;
        try {
            Thread.sleep(delaySeconds * 1000);
        } catch (InterruptedException e) {
        }
    }
}

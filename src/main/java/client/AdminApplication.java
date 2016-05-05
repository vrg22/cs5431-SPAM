import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.*;

public class AdminApplication extends ClientApplication
{

	public static final String ADMIN_TYPE = "admin";

    private int adminId; // ID of currently logged-in admin
    private String saltedAdminPassphrase; // Must accompany any privileged admin management action  //TODO: MAKE NULL when quit management mode!
    private AdminManagementFile adminFile; // Represents managed admins; set to null when done!
    //private byte[] adminSalt;
    //private String master;
    private AdminFrame frame;

	public AdminApplication() {
        gson = new Gson();
        frame = new AdminFrame(this);
		frame.start();
	}

	public static void main(String args[])
	{
		new AdminApplication();
	}


	/**
	 * Attempt to log in admin with specified credentials
	 *
	 * @return Was login successful
	 */
     public boolean login(String email, String password, String twoFactorCode) {
         // Request server for salt with user's email
         Map<String, String> saltParams = new HashMap<>();
         saltParams.put("email", email);
         String saltResponseJson;
         try {
             saltResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/salt",
                 saltParams);
         } catch (IOException e) {
             System.out.println("Problem connecting to server.");
             return false;
         }
         SaltResponse saltResponse = gson.fromJson(saltResponseJson,
             SaltResponse.class);
         if (saltResponse == null) return false;
         byte[] salt = saltResponse.getSalt();

         String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);

         // Request user for user's ID
         Map<String, String> authParams = new HashMap<>();
         authParams.put("type", ADMIN_TYPE);
         authParams.put("email", email);
         authParams.put("master", saltedHash);
         authParams.put("twoFactorCode", twoFactorCode);
         String authResponseJson;
         try {
             authResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/auth",
                 authParams);
         } catch (IOException e) {
             System.out.println("Problem connecting to server.");
             return false;
         }
         AuthResponse authResponse = gson.fromJson(authResponseJson,
             AuthResponse.class);
         if (authResponse == null) return false; // Failed login

         // Successful login
         adminId = authResponse.getId();

         return true;
 	}

    public void logout(boolean expired) {
        adminId = -1;
        frame.setPanel(new AdminLoginPanel(expired));
    }

    // Admin management functions
	// Model: Any time a change is made, persist it to (the server), revert? if failed
	// Do we want to only allow one "logged in guy" at a time?
    /**
     * Log in with admin management privileges.
     *
     * @return Was attempt successful
     */
    public boolean authManageAdmin(String adminPassword) {
    	// Request server for salt for admin Password //TODO: Should/can this be the same salt as for the startup adminpassphrase?
        Map<String, String> saltParams = new HashMap<>();
        saltParams.put("type", ADMIN_TYPE);
        String saltResponseJson;
        try {
            saltResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/admin/salt",
                saltParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        SaltResponse saltResponse = gson.fromJson(saltResponseJson,
            SaltResponse.class);
        if (saltResponse == null) return false;
        byte[] salt = saltResponse.getSalt();

        saltedAdminPassphrase = CryptoServiceProvider.genSaltedHash(adminPassword, salt);

        // Request server for admin list
        Map<String, String> authParams = new HashMap<>();
        authParams.put("type", ADMIN_TYPE);
        authParams.put("saltedHashAdmin", saltedAdminPassphrase);
        String authResponseJson;
        try {
            authResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/admin",
                authParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        
        //Deserialization
        //Type responseType = new TypeToken<AdminManageAuthResponse/*<AdminEntry>*/>() {}.getType();
        //AdminManageAuthResponse/*<AdminEntry>*/ authResponse = gson.fromJson(authResponseJson,
        //		responseType);
        AdminManageAuthResponse authResponse = gson.fromJson(authResponseJson,
        		AdminManageAuthResponse.class);
        if (authResponse == null) {
        	saltedAdminPassphrase = null;
        	return false;
        }

        // Successful login
        adminFile = authResponse.getAMFile();

        return true;
    }

	/**
     * Create new admin (NOTE: This "registration" should only be successfully invoked by an authorized admin manager)
     * This method updates the local copy of admins (Admin file) with the new admin IFF the admin was successfully registered on the server.
     *
     * @return Was admin successfully registered
     */
    public boolean register(String email, String password, String recovery,
            String twoFactorSecret) {
        byte[] salt = CryptoServiceProvider.getNewSalt();
        String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);
        String recoveryHash = CryptoServiceProvider.genSaltedHash(recovery, salt);

		String encPass = CryptoServiceProvider.encrypt(password, recovery, salt);
        byte[] recoverIV = CryptoServiceProvider.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("type", ADMIN_TYPE);
        params.put("username", email);
        params.put("salt", CryptoServiceProvider.b64encode(salt));
        params.put("saltedHash", saltedHash);
        params.put("encryptedPass", encPass);
        params.put("reciv", CryptoServiceProvider.b64encode(recoverIV));
        params.put("recoveryHash", recoveryHash);
        params.put("twoFactorSecret", twoFactorSecret);
        params.put("saltedHashAdmin", saltedAdminPassphrase); // Force this to be provided   //TODO: Check: what if null?

        String responseJson;
        try {
            responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class); //CHECK: Need to make new Response type for this situation?

        if (response != null && response.success()) {
        	// Add the admin to the local file IFF was a success at server
        	adminFile.putAdmin(new Admin(email, salt, saltedHash,
                Integer.parseInt(adminFile.getNextAdminID()), encPass, recoverIV,
                recoveryHash, twoFactorSecret));
            return true;
        } else {
        	//saltedAdminPassphrase = null;
            return false;
        }
    }

    /**
     * Obliterate logged-in admin
     *
     * @return Was admin successfully obliterated
     */
    public boolean obliterateAdmin(String username) {
    	/*
    	//TODO: How to get params in there for deletion? -> would we NEED to go to sessions like for rest of auth scheme? Would .delete even work then?
        Map<String, String> params = new HashMap<>();
        params.put("type", ADMIN_TYPE);
        params.put("saltedHashAdmin", saltedAdminPassphrase); // Force this to be provided
        */

        String responseJson;
        try {
            responseJson = SendHttpsRequest.delete(HTTPS_ROOT
                + "/admin/" + adminId);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        ObliterateResponse response = gson.fromJson(responseJson, ObliterateResponse.class);

        if (response.success()) {
        	// Remove admin from local file
        	adminFile.deleteAdmin(username);
            endAdminManagement();
            return true;
        } else {
            return false;
        }
    }

    // Admin management functions
	// Model: Any time a change is made, persist it to (the server), revert? if failed
	// Do we want to only allow one "logged in guy" at a time?

    /**
     * Get list of accounts associated with logged-in user
     *
     * @return Array of account headers for user's accounts
     */
    public Admin.Header[] getAdmins() {
    	// If not currently privileged...
        if (adminFile == null) return new Admin.Header[]{};

        return adminFile.getAdmins();
    }

    public boolean recoverPass(String email, String recovery, String twoFactorCode,
            String newPass) {
        Map<String, String> saltParams = new HashMap<>();
        saltParams.put("email", email);
        String saltResponseJson;
        try {
            saltResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/salt",
                saltParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        SaltResponse saltResponse = gson.fromJson(saltResponseJson,
            SaltResponse.class);
        if (saltResponse == null) return false;
        byte[] salt = saltResponse.getSalt();

        String saltedRecovery = CryptoServiceProvider.genSaltedHash(recovery, salt);

        // Request user for user's ID, IV, and encrypted vault
        Map<String, String> recoParams = new HashMap<>();
        recoParams.put("type", ADMIN_TYPE);
        recoParams.put("email", email);
        recoParams.put("recovery", saltedRecovery);
        recoParams.put("twoFactorCode", twoFactorCode);

        String recoResponseJson;
        try {
            recoResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/recover",
                recoParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }

        RecoResponse recoResponse = gson.fromJson(recoResponseJson,
            RecoResponse.class);
        if (recoResponse == null) return false; // Failed recovery

        String encPass = recoResponse.getEncPass();
        byte[] iv = recoResponse.getIV();

        String password = CryptoServiceProvider.decrypt(encPass, recovery, salt, iv);

        if (login(email, password, twoFactorCode)) {
            System.err.println("Password recovery successful");
        }

        return true;
	}

    //Delete admin from "Authorized" position

    //Add admin from "Authorized" position

    // Log out from an admin-manager's session
    public void endAdminManagement() {
    	saltedAdminPassphrase = null;
    	adminFile = null;
    }

}

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.util.*;
import com.google.gson.Gson;
import java.io.*;
import java.net.*;

public class AdminApplication extends ClientApplication
{
	
	public static final String ADMIN_TYPE = "admin";
	
    private int adminId; // ID of currently logged-in admin
    private boolean superAdmin; // True IFF authorized to perform admin management
    private AdminManagementFile adminFile;
    //private byte[] adminSalt;
    //private String master;

	public AdminApplication() {
        gson = new Gson();
		new AdminFrame(this).start();
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
	public boolean login(String email, String password) {
        // Request server for salt with admin's email
        Map<String, String> saltParams = new HashMap<>();
        saltParams.put("type", ADMIN_TYPE);
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

        // Request server for admin's ID
        Map<String, String> authParams = new HashMap<>();
        authParams.put("type", ADMIN_TYPE);
        authParams.put("email", email);
        authParams.put("master", saltedHash);
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
        if (authResponse == null) return false;

        // Successful login
        adminId = authResponse.getId();
        //adminSalt = salt;
        //master = password;

        return true;
	}
	
    public void logout() {
        adminId = -1;
    }
	
	/**
     * Register new admin, and log in with the new admin.
     *
     * @return Was admin successfully registered
     */
    public boolean register(String email, String password) {
        byte[] salt = CryptoServiceProvider.getNewSalt();
        String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);

        Map<String, String> params = new HashMap<>();
        params.put("type", ADMIN_TYPE);
        params.put("email", email);
        params.put("salt", CryptoServiceProvider.b64encode(salt));
        params.put("saltedHash", saltedHash);

        String responseJson;
        try {
            responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);

        if (response != null && response.success()) {
            login(email, password);

            return true;
        } else {
            return false;
        }
    }
	
    
    /**
     * Obliterate logged-in admin
     *
     * @return Was admin successfully obliterated
     */
    public boolean obliterateAdmin() {
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
            logout();

            return true;
        } else {
            return false;
        }
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

        String saltedHash = CryptoServiceProvider.genSaltedHash(adminPassword, salt);

        // Request server for admin list
        Map<String, String> authParams = new HashMap<>();
        authParams.put("type", ADMIN_TYPE);
        authParams.put("saltedHash", saltedHash);
        String authResponseJson;
        try {
            authResponseJson = SendHttpsRequest.post(HTTPS_ROOT + "/admin",
                authParams);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        AdminManageAuthResponse authResponse = gson.fromJson(authResponseJson,
        		AdminManageAuthResponse.class);
        if (authResponse == null) return false;

        // Successful login
        adminFile = authResponse.getAMFile();

        return true;
    }
    
    /**
     * Get list of accounts associated with logged-in user
     *
     * @return Array of account headers for user's accounts
     */
    public Admin.Header[] getAdmins() {
        if (adminFile == null) return new Admin.Header[]{};

        return adminFile.getAdmins();
    }
    
    //Delete admin from "Authorized" position
    
    //Add admin from "Authorized" position
    
    //
	
}

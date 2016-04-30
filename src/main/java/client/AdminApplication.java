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

         int id = authResponse.getId();

         // Successful login
         adminId = authResponse.getId();

         return true;
 	}

     public void logout(boolean expired) {
         adminId = -1;
         frame.setPanel(new AdminLoginPanel(expired));
     }

	/**
     * Register new admin, and log in with the new admin.
     *
     * @return Was admin successfully registered
     */
    fd
    public boolean register(String email, String password, String recovery,
            String twoFactorSecret) {
        byte[] salt = CryptoServiceProvider.getNewSalt();
        String saltedHash = CryptoServiceProvider.genSaltedHash(password, salt);
        String recoveryHash = CryptoServiceProvider.genSaltedHash(recovery, salt);

        StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?
        store.setupUserXML(xmlStringBuilder, 0); // TODO: set user ID (or get rid of in user XML files?)
        String decryptedVault = xmlStringBuilder.toString();
        String encVault = CryptoServiceProvider.encrypt(decryptedVault, password, salt);
        byte[] iv = CryptoServiceProvider.getIV();

		String encPass = CryptoServiceProvider.encrypt(password, recovery, salt);
        byte[] recoverIV = CryptoServiceProvider.getIV();

        Map<String, String> params = new HashMap<>();
        params.put("type", ADMIN_TYPE);
        params.put("email", email);
        params.put("salt", CryptoServiceProvider.b64encode(salt));
        params.put("saltedHash", saltedHash);
        params.put("vault", encVault);
        params.put("iv", CryptoServiceProvider.b64encode(iv));
        params.put("encryptedPass", encPass);
        params.put("reciv", CryptoServiceProvider.b64encode(recoverIV));
        params.put("recoveryHash", recoveryHash);
        params.put("twoFactorSecret", twoFactorSecret);

        String responseJson;
        try {
            responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/register", params);
        } catch (IOException e) {
            System.out.println("Problem connecting to server.");
            return false;
        }
        RegisterResponse response = gson.fromJson(responseJson, RegisterResponse.class);

        return response != null && response.success();
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
                + "/admins/" + adminId);
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


}

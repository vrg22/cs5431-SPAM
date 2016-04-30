import com.google.gson.Gson;

public abstract class ClientApplication
{
	public static final String HTTPS_ROOT = "https://ec2-52-91-199-182.compute-1.amazonaws.com:4567";
	protected static final int PASSWORD_LENGTH = 12;
    protected Gson gson;

	/**
	 * Attempt to log in with specified credentials
	 *
	 * @return Was login successful
	 */
	public abstract boolean login(String email, String password);

    public abstract void logout();

    /**
     * Register new user, and log in with the new user.
     *
     * @return Was user successfully registered
     */
    public abstract boolean register(String email, String password);

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
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

        if (resetPass(email, password, twoFactorCode, newPass, recovery)) {
            System.err.println("Password recovery successful");
        }

        return true;
	}

	public boolean resetPass(String email, String curPass, String twoFactorCode,
            String newPass, String recovery) {
	  if (login(email, curPass, twoFactorCode)) {
	  //if (curPass.equals(master)) {
		// re-encrypt with new master pass
		master = newPass;
		if (saveVault()) {
		  String saltedHash = CryptoServiceProvider.genSaltedHash(newPass, userSalt);
		  String encPass = CryptoServiceProvider.encrypt(newPass, recovery, userSalt);
		  byte[] recoverIV = CryptoServiceProvider.getIV();

		  Map<String, String> params = new HashMap<>();
		  params.put("saltedHash", saltedHash);
		  params.put("encryptedPass", encPass);
		  params.put("reciv", CryptoServiceProvider.b64encode(recoverIV));
		  params.put("authKey", authKey);
		  String nextAuthKey = CryptoServiceProvider.genRequestAuthKey();
		  params.put("nextAuthKey", nextAuthKey);
		  System.err.println("resetPass authKey:" +authKey);

		  String responseJson;
		  try {
			responseJson = SendHttpsRequest.post(HTTPS_ROOT + "/users/"
				+ userId + "/resetpass", params);
		  } catch (IOException e) {
			System.out.println("Problem connecting to server.");
			return false;
		  }
		  SaveResponse response = gson.fromJson(responseJson, SaveResponse.class);

		  if (response.success()) {
			authKey = nextAuthKey;
			System.out.println("resetPass responded true");
			return true;
		  } else {
			System.out.println("resetPass responded false");
            logout(true);
			return false;
		  }
		}
	  }
	  System.out.println("resetPass login failed");
	  return false;
	}
}

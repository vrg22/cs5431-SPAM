// Keys: user ID, username, hashed-salted master password
public class PasswordStorageEntry extends StorageEntry {
    // Note: master password here should already be hashed
    public PasswordStorageEntry(User user) {
        super();

        put("userid", ""+user.getId());
        put("username", user.getUsername());
		put("iv", CryptoServiceProvider.b64encode(user.getIV()));
		put("salt", CryptoServiceProvider.b64encode(user.getSalt()));
        put("master", user.getMaster());
        put("encpass", user.getEncPass());
        put("reciv", CryptoServiceProvider.b64encode(user.getRecIV()));
        put("recovery", user.getRecovery());
        put("twoFactorSecret", user.getTwoFactorSecret());
    }

    public int getUserId() {
        return Integer.parseInt(get("userid"));
    }

    public String getUsername() {
        return get("username");
    }

    public String getMaster() {
        return get("master");
    }

	public byte[] getSalt() {
		return CryptoServiceProvider.b64decode(get("salt"));
	}

	public byte[] getIV() {
		return CryptoServiceProvider.b64decode(get("iv"));
	}

	public byte[] getRecIV() {
	  return CryptoServiceProvider.b64decode(get("reciv"));
	}

	public String getRecovery() {
	  return get("recovery");
	}

	public String getEncPass() {
	  return get("encpass");
	}

    public void setIV(String iv) {
        update("iv", iv);
    }

    public void setRecIV(String iv) {
        update("reciv", iv);
    }

	public void setMaster(String hashPass) {
	  update("master", hashPass);
	}

	public void setEncPass(String hashPass) {
	  update("encpass", hashPass);
	}

    public String getTwoFactorSecret() {
        return get("twoFactorSecret");
    }

    public void setTwoFactorSecret(String secret) {
        update("twoFactorSecret", secret);
    }
}

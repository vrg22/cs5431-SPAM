// Keys: ID, username, hashed-salted master password
public class PasswordStorageEntry extends StorageEntry {
    // Note: master password here should already be hashed
    public PasswordStorageEntry(Client client) {
        super();

        String type = (client instanceof User) ? "user" :
        			  (client instanceof Admin) ? "admin" : "other";
        put("type", type);
        put("id", ""+client.getId());
        put("username", client.getUsername());
		put("iv", (client.getIV()==null) ? null : CryptoServiceProvider.b64encode(client.getIV()));
		put("salt", CryptoServiceProvider.b64encode(client.getSalt()));
        put("master", client.getMaster());
        put("encpass", client.getEncPass());
        put("reciv", CryptoServiceProvider.b64encode(client.getRecIV()));
        put("recovery", client.getRecovery());
        put("twoFactorSecret", client.getTwoFactorSecret());
    }

    public String getType() {
        return get("type");
    }

    public int getId() {
        return Integer.parseInt(get("id"));
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
		return (get("iv")!=null) ? CryptoServiceProvider.b64decode(get("iv")) : null;
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

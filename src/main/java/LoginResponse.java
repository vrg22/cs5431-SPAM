public class LoginResponse {

    private int userId;
    private byte[] salt;
    private byte[] iv;
    private String saltedHash;
    private String vault;

    public LoginResponse(int userId, String vault, String saltedHash, byte[] salt, byte[] iv) {
        this.userId = userId;
        this.salt = salt.clone();
        this.iv = iv.clone();
        this.saltedHash = saltedHash;
        this.vault = vault;
    }

    public int getId() {
        return userId;
    }

    public byte[] getSalt() {
        return salt.clone();
    }

    public byte[] getIV() {
        return iv.clone();
    }

    public String getSaltedHash() {
        return saltedHash;
    }

    public String getVault() {
        return vault;
    }
}

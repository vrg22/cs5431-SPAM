public class LoginResponse {

    private int userId;
    private byte[] salt;
    private String iv;
    private String saltedHash;
    private String vault;

    public LoginResponse(int userId, String vault, String saltedHash, byte[] salt, String iv) {
        this.userId = userId;
        this.salt = salt;
        this.iv = iv;
        this.saltedHash = saltedHash;
        this.vault = vault;
    }

    public int getId() {
        return userId;
    }

    public byte[] getSalt() {
        return salt;
    }

    public String getIV() {
        return iv;
    }

    public String getSaltedHash() {
        return saltedHash;
    }

    public String getVault() {
        return vault;
    }
}

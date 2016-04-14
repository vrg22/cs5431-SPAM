public class AuthResponse {

    private int userId;
    private byte[] iv;
    private String vault;

    public AuthResponse(int userId, String vault, byte[] iv) {
        this.userId = userId;
        this.iv = iv.clone();
        this.vault = vault;
    }

    public int getId() {
        return userId;
    }

    public byte[] getIV() {
        return iv.clone();
    }

    public String getVault() {
        return vault;
    }
}

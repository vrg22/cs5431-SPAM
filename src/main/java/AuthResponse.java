public class AuthResponse {

    private int id;
    private byte[] iv;
    private String vault; //null if not a user

    public AuthResponse(int id, String vault, byte[] iv) {
        this.id = id;
        this.iv = iv != null ? iv.clone() : null;
        this.vault = vault;
    }

    public int getId() {
        return id;
    }

    public byte[] getIV() {
        return iv.clone();
    }

    public String getVault() {
        return vault;
    }
}

public class SaltResponse {

    private byte[] salt;

    public SaltResponse(byte[] salt) {
        this.salt = salt.clone();
    }

    public byte[] getSalt() {
        return salt.clone();
    }
}

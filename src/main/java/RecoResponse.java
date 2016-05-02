public class RecoResponse {

    private byte[] recoiv;
    private String encPass;

    public RecoResponse(String encPass, byte[] iv) {
        this.recoiv = iv.clone();
        this.encPass = encPass;
    }

    public byte[] getIV() {
        return recoiv.clone();
    }

    public String getEncPass() {
        return encPass;
    }
}

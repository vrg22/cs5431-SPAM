public class DeleteLogResponse {

    private boolean success;

    public DeleteLogResponse(boolean success) {
        this.success = success;
    }

    public boolean success() {
        return success;
    }
}

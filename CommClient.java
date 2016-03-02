/**
 * Transfers data between client UI and server communication module
 */
public interface CommClient {
    public void send(Object data);
    public Object receive();
}

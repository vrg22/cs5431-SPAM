/**
 * Transfers data between client UI and server communication module
 */
public interface CommClient {
    // TODO: decide what type this data should be
    public void send(Object data);
    public Object receive();
}

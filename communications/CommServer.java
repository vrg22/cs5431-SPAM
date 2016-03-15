package communications;
/**
 * Transfers data between server communication module and storage
 */
import java.util.*;
import java.io.*;
import java.net.*;

//class CommServer implements Communications {
public class CommServer implements Communications {
	private String hostName;
	private int portNo;
	private Socket connection;
	private ServerSocket server;
	private ObjectOutputStream commOutputStream;
	private ObjectInputStream commInputStream;

	private CommServer() {
		System.out.println("Unused");
	}

	public CommServer(String host, int port) {
		this.hostName = host;
		this.portNo = port;
	}

	public boolean makeConnection() {
		boolean status = false;
		try {
			server = new ServerSocket(portNo);

			/*
			 * TODO: Move the accept listener to its own thread
			 */
			connection = server.accept();
			commOutputStream = new ObjectOutputStream(connection.
					getOutputStream());

			commInputStream = new ObjectInputStream(connection.
					getInputStream());
			status = true;
		} catch (Exception e) {
			System.err.println("Error connecting to server");
		}
		return status;
	}

	public void send(Message data) {
		try {
			commOutputStream.writeObject(data);
		} catch (Exception e) {
			System.err.println("Error sending message to server");
		}
	}

	public Message receive() {
		Message msg = null;
		try {
			System.out.println("Blocking for read");
			msg = (Message) commInputStream.readObject();
			System.out.println("Unblocking after read");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException reading object");
		} catch (InvalidClassException e) {
			System.err.println("InvalidClassException reading object");
		} catch (StreamCorruptedException e) {
			System.err.println("StreamCorruptedException reading object");
		} catch (OptionalDataException e) {
			System.err.println("OptionalDataException reading object");
		} catch (IOException e) {
			System.err.println("IO Error reading object");
		} catch (Exception e) {
			System.err.println("Error reading from the socket");
		}

		return msg;
	}

	public void destroyConnection() {
		try {
			commOutputStream.close();
			commInputStream.close();
			connection.close();
			server.close();
		} catch (Exception e) {
			System.err.println("Error closing the connection");
		}
	}

	/*
	 * Uncomment for testing
	public static void main(String args[]) {
		CommServer cs = new CommServer("localhost", 5998);

		while (true) {
			if (cs.makeConnection()) {
				while (true) {
					Message m = cs.receive();

					if (m != null) {
						if (m.getQuery().equals("REGISTER")) {
							RegisterMessage rm = (RegisterMessage) m;
							System.out.println("Received values");
							System.out.println(rm.getSequence());
							System.out.println(rm.getVersion());
							System.out.println(rm.getUsername());
							System.out.println(rm.getPassword());
						} else if (m.getQuery().equals("LOGIN")) {
							LoginMessage lm = (LoginMessage) m;
							System.out.println("Received values");
							System.out.println(lm.getSequence());
							System.out.println(lm.getVersion());
							System.out.println(lm.getUsername());
							System.out.println(lm.getPassword());
						}
					} else {
						cs.destroyConnection();
						break;
					}
				}
			}
		}
	}*/
}

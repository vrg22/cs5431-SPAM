/**
 * Transfers data between server communication module and storage
 */
import java.util.*;
import java.io.*;
import java.net.*;

//class CommServer implements Communications {
public class CommServer {
	private String hostName;
	private int portNo;
	private Socket connection;
	private ServerSocket server;
	private DataOutputStream commOutputStream;
	private DataInputStream commInputStream;

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
			commOutputStream = new DataOutputStream(connection.
					getOutputStream());

			commInputStream = new DataInputStream(connection.
					getInputStream());
			status = true;
		} catch (Exception e) {
			System.err.println("Error connecting to server");
		}
		return status;
	}

	private byte[] convertToBytes(Message object) throws IOException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream out = new ObjectOutputStream(bos)) {
			out.writeObject(object);
			return bos.toByteArray();
				}
	}

	private Message convertFromBytes(byte[] bytes) throws IOException,
			ClassNotFoundException {
				try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
						ObjectInputStream in = new ObjectInputStream(bis)) {
					return ((Message)in.readObject());
						}
	}

	public void send(Message m) throws IOException {
		byte[] message = convertToBytes(m);
		sendOverNetwork(message, message.length);
	}

	public Message receive() throws IOException, ClassNotFoundException {
		byte[] message = recvOverNetwork();
		if (message != null) {
			Message m = convertFromBytes(message);
			return m;
		} else {
			return null;
		}
	}

	public void sendOverNetwork(byte[] data, int len) {
		try {
			if (len > 0) {
				commOutputStream.writeInt(len);
				commOutputStream.write(data, 0, len);
			}
		} catch (Exception e) {
			System.err.println("Error sending message to server");
		}
	}

	public byte[] recvOverNetwork() {
		try {
			int len = commInputStream.readInt();
			//System.out.println("len :" +len);
			if (len > 0) {
				byte[] msg = new byte[len];
				commInputStream.read(msg, 0, len);
				return msg;
			}
		} catch (IOException e) {
			System.err.println("IO Error reading object");
		} catch (Exception e) {
			System.err.println("Error reading from the socket");
		}
		return null;
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
	public static void main(String args[]) throws IOException,
		   ClassNotFoundException {
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
							System.out.println(lm.getAttemptsRemaining());
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

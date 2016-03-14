/**
 * Transfers data between client UI and server communication module
 */
import java.util.*;
import java.io.*;
import java.net.*;

//class CommClient implements Communications {
public class CommClient {
	private String hostName;
	private int portNo;
	private Socket connection;
	private DataInputStream commInputStream;
	private DataOutputStream commOutputStream;

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

	private CommClient() {
		System.out.println("Unused");
	}

	public CommClient(String host, int port) {
		this.hostName = host;
		this.portNo = port;
	}

	public boolean makeConnection() {
		boolean status = false;
		try {
			InetAddress address = InetAddress.getByName(hostName);
			connection = new Socket(address, portNo);

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
		} catch (Exception e) {
			System.err.println("Error closing the connection");
		}
	}

	/*
	 * Uncomment for testing
	public static void main(String args[]) throws IOException,
		   InterruptedException {
		CommClient client = new CommClient("localhost", 5998);
		if (client.makeConnection()) {

			RegisterMessage rm = new RegisterMessage("newuser", "newpassword");

			client.send(rm);

			LoginMessage lm = new LoginMessage("tempuser", "temppassword");
			client.send(lm);

			Thread.sleep(10000);
			client.destroyConnection();
		}
	}*/
}

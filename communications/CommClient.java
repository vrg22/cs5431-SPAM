package communications;
/**
 * Transfers data between client UI and server communication module
 */
import java.util.*;
import java.io.*;
import java.net.*;

//class CommClient implements Communications {
public class CommClient implements Communications {
	private String hostName;
	private int portNo;
	private Socket connection;
	private ObjectOutputStream commOutputStream;
	private ObjectInputStream commInputStream;
	private Message savedMessage;

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

	// Temporarily cache an unfinished message
	// to save state between related menus
	public void save(Message data) {
		savedMessage = data;
	}

	public Message getSaved() {
		return savedMessage;
	}

	public void send(Message data) {
		try {
			commOutputStream.writeObject(data);
			savedMessage = null;
		} catch (Exception e) {
			System.err.println("Error sending message to server");
		}
	}

	public Message receive() {
		Message msg = null;
		try {
			msg = (Message) commInputStream.readObject();
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
		} catch (Exception e) {
			System.err.println("Error closing the connection");
		}
	}

	/*
	 * Uncomment for testing
	public static void main(String args[]) {
		CommClient client = new CommClient("localhost", 5998);
		if (client.makeConnection()) {

			RegisterMessage rm = new RegisterMessage("newuser", "newpassword");

			client.send(rm);

			LoginMessage lm = new LoginMessage("tempuser", "temppassword");
			client.send(lm);
			client.destroyConnection();
		}
	}*/
}

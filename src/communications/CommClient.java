package communications;

/**
 * Transfers data between client UI and server communication module
 */

import java.util.*;
import java.io.*;
import java.net.*;

public class CommClient implements Communications {
	private String hostName;
	private int portNo;
	private Socket connection;
	private DataInputStream commInputStream;
	private DataOutputStream commOutputStream;
	private Message savedMessage;

	private byte[] convertToBytes(Message object) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(bos); 
		
		out.writeObject(object);
		return bos.toByteArray();
	}

	private Message convertFromBytes(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInputStream in = new ObjectInputStream(bis);
		return ((Message)in.readObject());
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
		} catch (IOException e) {
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

	public void send(Message m) {
		try {
			savedMessage = null;
			byte[] message = convertToBytes(m);
			sendOverNetwork(message, message.length);
		} catch (IOException e) {
			System.err.println("Error sending over network");
		}
	}

	public Message receive() {
		try {
			byte[] message = recvOverNetwork();
			if (message != null) {
				Message m = convertFromBytes(message);
				return m;
			} else {
				return null;
			}
		} catch (IOException e) {
			System.err.println("IOException receiving messages over network");
		} catch (ClassNotFoundException e) {
			System.err.println("ClassNotFoundException receiving messages over network");
		}

		return null;
	}

	public void sendOverNetwork(byte[] data, int len) {
		try {
			if (len > 0) {
				commOutputStream.writeInt(len);
				commOutputStream.write(data, 0, len);
			}
		} catch (Exception e) {
			//e.printStackTrace();
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

			Message.RegisterMessage rm = new Message.RegisterMessage("newuser",
					"newpassword");

			client.send(rm);

			Message.LoginMessage lm = new Message.LoginMessage("tempuser",
					"temppassword");
			client.send(lm);

			Thread.sleep(10000);
			client.destroyConnection();
		}
	} */
}

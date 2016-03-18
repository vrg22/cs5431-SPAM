package communications;

/**
 * Transfers data between server communication module and storage
 */

import java.util.*;

import communications.Message.Response;
import server.StoreAndRetrieveUnit;

import java.io.*;
import java.net.*;
import java.lang.Thread;

public class CommServer {
	private String hostName;
	private int portNo;
	private ServerSocket server;

	public CommServer(String host, int port) {
		this.hostName = host;
		this.portNo = port;
	}

	public void makeConnection() {
		boolean status = false;
		try {
			server = new ServerSocket(portNo);

			while (true) {
				ClientWorker w;
				try {
					w = new ClientWorker(server.accept());
					Thread t = new Thread(w);
					t.start();
				} catch (Exception e) {
					System.err.println("Error accepting incoming connection");
				}
			}
		} catch (IOException e) {
			System.err.println("Error connecting to client");
		}
	}

	public void destroyConnection() {
		if (server == null) {
			System.err.println("Error closing non-existent connection");
			return;
		}
		
		try {
			server.close();
		} catch (IOException e) {
			System.err.println("Error closing the connection");
		}
	}

	private static class ClientWorker implements Runnable, Communications {
		private DataOutputStream commOutputStream;
		private DataInputStream commInputStream;
		private Socket sock;
		private StoreAndRetrieveUnit sru = new StoreAndRetrieveUnit(); //Final?

		public ClientWorker(Socket sock) {
			this.sock = sock;
			try {
				commOutputStream = new DataOutputStream(sock.getOutputStream());
				commInputStream = new DataInputStream(sock.getInputStream());
			} catch (IOException e) {
				System.err.println("Error getting input output streams");
			} catch (Exception e) {
				System.err.println("Exception when setting up ClientWorker");
			}
		}

		public void run() {
			while (true) {
				Message m = receive();
				
				//call the storageandretrieve unit
				// Gets a replyMessage from StorageAndRetrieve
				Response r = sru.processMessage(m);
				send(r);
				
				if (m != null) {
					if (m.getQuery().equals("REGISTER") && m instanceof Message.RegisterMessage) {
						Message.RegisterMessage rm =
							(Message.RegisterMessage) m;
						System.out.println("Received values");
						System.out.println(rm.getSequence());
						System.out.println(rm.getVersion());
						System.out.println(rm.getUsername());
						System.out.println(rm.getPassword());
					} else if (m.getQuery().equals("LOGIN") && m instanceof Message.LoginMessage) {
						Message.LoginMessage lm =
							(Message.LoginMessage) m;
						System.out.println("Received values");
						System.out.println(lm.getSequence());
						System.out.println(lm.getVersion());
						System.out.println(lm.getUsername());
						System.out.println(lm.getPassword());
						System.out.println(lm.getAttemptsRemaining());
					}
				} else {
					try {
						sock.close();
					} catch (IOException e) {
						System.err.println("Error closing the connection");
					}
					break;
				}

			}
		}

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

		public void send(Message m) {
			// Don't send null across the network
			if (m == null) {
				System.err.println("Tried to send empty message over network");
				return;
			}
			
			try {
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

		private void sendOverNetwork(byte[] data, int len) {
			try {
				if (len > 0) {
					commOutputStream.writeInt(len);
					commOutputStream.write(data, 0, len);
				}
			} catch (Exception e) {
				System.err.println("Error sending message to client");
			}
		}

		private byte[] recvOverNetwork() {
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

	}
	/*
	 * Uncomment for testing
	 */
	 public static void main(String args[]) throws IOException,
			ClassNotFoundException {
				CommServer cs = new CommServer("localhost", 5998);

				while (true) {
					cs.makeConnection();
				}
	 }
}

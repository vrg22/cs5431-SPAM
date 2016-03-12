/* Stores and retrieves all information related to user accounts and data. Writes important information to log.
 * This class is the main point of entry for the server-side communication unit (CommServer.java).
 */

import java.util.*;
import java.io.*;
import java.net.*;

//Imported for XML parsing by the DOM method
//Adapted from http://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;


public class StoreAndRetrieveUnit {
	//Communication with CommServer
	private String hostName;
	private int portNo;
	private Socket connection;
	private ServerSocket server;
	private ObjectOutputStream commOutputStream;
	private ObjectInputStream commInputStream;
	//XML
	private Document DOM;
	//Storage
	public static final String PASSWORD_FILE_LOCATION = "password.xml"; //"storage.json";
	//Logging
	public static final String LOG_FILE_LOCATION = "log.txt";
	
	//Constructors
	private StoreAndRetrieveUnit() {
		System.out.println("Unused");
	}
	
	public StoreAndRetrieveUnit(String host, int port) {
		this.hostName = host;
		this.portNo = port;
	}
	
	
	//Generic function to interpret a message received from CommServer
	public void processMessage(Message m) {
		//TODO is this bad practice here?
		if (m instanceof RegisterMessage){
			RegisterMessage reg_m = (RegisterMessage) m;
			register_new_user(reg_m);
		}
		else if (m instanceof LoginMessage) {
			LoginMessage log_m = (LoginMessage) m;
			login_user(log_m);
		}
		else if (m instanceof ListingMessage) {
			ListingMessage list_m = (ListingMessage) m;
			list_items(list_m);
		}
		else if (m instanceof RetrieveIdMessage) {
			RetrieveIdMessage retr_m = (RetrieveIdMessage) m;
			retrieve_userID(retr_m);
		}
		else if (m instanceof EditIdMessage) {
			EditIdMessage edit_m = (EditIdMessage) m;
			edit_userID(edit_m);
		}
		else if (m instanceof DeleteIdMessage) {
			DeleteIdMessage del_m = (DeleteIdMessage) m;
			delete_userID(del_m);
		}
		else if (m instanceof ObliterateMessage) {
			ObliterateMessage obl_m = (ObliterateMessage) m;
			obliterate(obl_m);
		}
		
		//Call appropriate helper method
		//Throw exception if something unexpected
	}
	
	
	//Basic file-methods
	
	
	//XML DOM is read in, modify, then write back.
	//So is XML even a good choice???
	//Then, if creating a new file, create it, but check if file exists first
	//Function that creates XML if DNE, writes basic info. otherwise, nothing
	//Function that loads existing XML into DOM, ready for editing
	//Function that saves existing DOM and closes everything
	//WHAT vars need to be IN this class? The main DOM, anything else that we dont wanna create/destroy for one session
	/*
	 * DOM
	 * String
	 * 
	 */
	//Idea of THREADS making diff modifications to the elements....
	
	/**
	 * Returns a handle to a newly-created file with the specified name.
	 * Throws exception if file creation failed.
	 * @return
	 */
	public void createXMLFile(String name){
        File newFile = new File(name);
        createDOM();        //Set up the file and let DOM equal the file
	}
	
	//deleteFile()

	
	//XML File parsing methods
	//At all times, have entirety of the password file in memory???
	//Or is the event-based triggering in SAX more secure?
	//When to do the encryption? Easier/more efficient to encrypt parts of the message or whole thing?
	
	/**
	 * Sets a newly-created XML file up with application-specific fields are inserted.
	 * Throws exception if file creation failed.
	 * @return
	 */
	private void createDOM() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
	
		//Create XML base class, get doc
		StringBuilder xmlStringBuilder = new StringBuilder();
		xmlStringBuilder.append("<?xml version=\"1.0\"?> <class> LMAOOOOO </class>");
		ByteArrayInputStream input =  new ByteArrayInputStream(
		   xmlStringBuilder.toString().getBytes("UTF-8"));
		DOM = builder.parse(input);
		
		Element root = DOM.getDocumentElement();
		System.out.println("BLA: " + root.getAttribute("class"));
		
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	

	
	//METHODS FOR STORAGE
	//storePassword()
	
	//METHODS FOR RETRIEVAL
	//retrieve(Message m)
	
	
	private void register_new_user(RegisterMessage reg_m) {
		// TODO Auto-generated method stub
		
	}
	
	private void login_user(LoginMessage log_m) {
		// TODO Auto-generated method stub
		
	}
	
	private void list_items(ListingMessage list_m) {
		// TODO Auto-generated method stub
		
	}

	private void retrieve_userID(RetrieveIdMessage retr_m) {
		// TODO Auto-generated method stub
		
	}

	private void edit_userID(EditIdMessage edit_m) {
		// TODO Auto-generated method stub
		
	}

	private void delete_userID(DeleteIdMessage del_m) {
		// TODO Auto-generated method stub
		
	}

	private void obliterate(ObliterateMessage obl_m) {
		// TODO Auto-generated method stub
		
	}
	

	//METHODS FOR COMMUNICATION - copied from CommServer.java
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
	
	
	//Testing
	public static void main(String[] args) {
		System.out.println("TESTING...");
		StoreAndRetrieveUnit sru = new StoreAndRetrieveUnit("localhost", 5999);
		sru.createXMLFile("fool.xml");
	}

}

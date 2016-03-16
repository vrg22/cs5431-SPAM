package server;


/* Stores and retrieves all information related to user accounts and data. Writes important information to log.
 * This class is the main point of entry for the server-side communication unit (CommServer.java).
 */

import java.io.*;
//import java.util.*;
//import java.net.*;
import java.util.ArrayList;
//Imported for logging by default logging (java.util.logging)
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Imported for XML parsing by the DOM method
//Adapted from http://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerConfigurationException;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;

import communications.*;
import communications.Message.*;

public class StoreAndRetrieveUnit {
	//Communication with CommServer
	//private String hostName;
	//private int portNo;
	//private ServerSocket server; //The socket at which I sit
	//private Socket connection;  // Will need one of these for each ClientWorker that tries to connect to me
	//private ObjectOutputStream commOutputStream;
	//private ObjectInputStream commInputStream;
	
	//XML
	private Document DOM;
	//Storage
	public static final String MAIN_FILE_LOCATION = "password.xml"; //"storage.json";
	//Logging
	public static final String LOG_FILE_LOCATION = "log.log";
	private static final Logger logger = Logger.getLogger(StoreAndRetrieveUnit.class.getName()); //NAME??
	
	//Constructors
	public StoreAndRetrieveUnit() {
		
		//Set up logging
		try {

	        // Configure location and log formatting  			
	        FileHandler fh = new FileHandler(LOG_FILE_LOCATION, true);
	        logger.addHandler(fh);
	        SimpleFormatter formatter = new SimpleFormatter();
	        fh.setFormatter(formatter);  

	        // Initial log message
	        logger.info("Starting up SPAM...");

	    } catch (SecurityException e) {  
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }
	}
	
	
	
	//Generic function to interpret a message received from CommServer
	public Response processMessage(Message m) {
		//TODO is instanceof bad practice here?
		
		Response reply = null;
		
		if (m instanceof RegisterMessage){
			RegisterMessage reg_m = (RegisterMessage) m;
			reply = register_new_user(reg_m);
		}
		else if (m instanceof LoginMessage) {
			LoginMessage log_m = (LoginMessage) m;
			reply = login_user(log_m);
		}
		else if (m instanceof ListingMessage) {
			ListingMessage list_m = (ListingMessage) m;
			reply = list_items(list_m);
		}
		else if (m instanceof RetrieveIdMessage) {
			RetrieveIdMessage retr_m = (RetrieveIdMessage) m;
			reply = retrieve_userID(retr_m);
		}
		else if (m instanceof EditIdMessage) {
			EditIdMessage edit_m = (EditIdMessage) m;
			reply = edit_userID(edit_m);
		}
		else if (m instanceof DeleteIdMessage) {
			DeleteIdMessage del_m = (DeleteIdMessage) m;
			reply = delete_userID(del_m);
		}
		else if (m instanceof ObliterateMessage) {
			ObliterateMessage obl_m = (ObliterateMessage) m;
			reply = obliterate(obl_m);
		}

		//Throw exception if something unexpected
				
		return reply;
	}
	
	
	//METHODS FOR STORAGE
		//storePassword()
		
	//METHODS FOR RETRIEVAL
	//retrieve(Message m)
	
	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response register_new_user(RegisterMessage reg_m) {
		// Unpack message, find out who is trying to register
		String uName = null;
		String pWord = null;
		
		// Try to register
		String respCode = null;
		logger.log(Level.INFO, "User " + uName + "tried to register");
		
		// Determine and construct Response	
		//logger.log(Level.INFO, "Created new user");
		RegisterResponse reply = new RegisterResponse(uName, pWord, respCode);
		return reply;
	}
	
	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response login_user(LoginMessage log_m) {
		// Unpack message, find out who is trying to log in
		String uName = null;
		String pWord = null;
		
		// Try to log them in
		String respCode = null;
		logger.log(Level.INFO, "User " + uName + "tried to log in");
		
		// Determine and construct Response
		LoginResponse reply = new LoginResponse(uName, pWord, respCode);
		return reply;
	}
	
	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response list_items(ListingMessage list_m) {
		// Unpack message, find out who wants a listing
		String uName = null;
		String pWord = null;
		
		// Try to obtain listing
		String respCode = null;
		ArrayList<Record> listing = null; // = get Listing from XML
		logger.log(Level.INFO, "User " + uName + " requesting full record listing");
		
		// Determine and construct Response
		ListingResponse reply = new ListingResponse(uName, pWord, respCode, listing);
		return reply;
	}

	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response retrieve_userID(RetrieveIdMessage retr_m) {
		// Unpack message, find out who wants to retrieve a particular record
		String uName = null;
		String pWord = null;
		int id = 0; //FIGURE OUT

		// Try to obtain record
		String respCode = null;
		Record rec = null; // "FIND OUT";
		logger.log(Level.INFO, "User " + uName + " requesting record " + ""); //record's name
		
		// Determine and construct Response
		RetrieveIdResponse reply = new RetrieveIdResponse(uName, pWord, respCode, id, rec);
		return reply;
	}

	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response edit_userID(EditIdMessage edit_m) {
		// Unpack message, find out who wants to edit a particular record
		String uName = null;
		String pWord = null;
		int id = 0; //FIGURE OUT
		String action = "FILL IN";
		String item = "FIND OUT";

		// Try to modify record
		String respCode = null;
		//Record rec = null; // "FIND OUT";
		logger.log(Level.INFO, "User " + uName + " requests to " + action + " the record corresponding to " + item);
		
		// Determine and construct Response
		EditIdResponse reply = new EditIdResponse(uName, pWord, respCode, id);
		return reply;
	}

	/**
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response delete_userID(DeleteIdMessage del_m) {
		// Unpack message, find out who wants to delete a particular record
		String uName = null;
		String pWord = null;
		int id = 0; //FIGURE OUT
		String action = "FILL IN";
		String item = "FIND OUT";

		// Try to modify record
		String respCode = null;
		logger.log(Level.INFO, "User " + uName + " requests to PERMANENTLY DELETE the record corresponding to " + item);
		
		// Determine and construct Response
		DeleteIdResponse reply = new DeleteIdResponse(uName, pWord, respCode, id);
		//logger.log(Level.INFO, "User " + uName + " deleted record for " + item);
		return reply;
	}

	/**
	 * Delete's an entire user's records based on the message.
	 * Sends/returns a Response message back to the host ...
	 * Throws exception if ...
	 * @return
	 */
	private Response obliterate(ObliterateMessage obl_m) {
		// Unpack message, find out who wants to delete their entire vault
		String uName = null;
		String pWord = null;
		
		// Try to modify record
		String respCode = null;
		logger.log(Level.WARNING, "User " + uName + " requesting deletion of entire record directory"); // Change to INFO?
		
		// Determine and construct Response
		ObliterateResponse reply = new ObliterateResponse(uName, pWord, respCode);
		//logger.log(Level.INFO, "User " + uName + " deleted record for " + item);
		return reply;
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
	 * Do not save the file here; DOM will be set and ready for modifications.
	 * Throws exception if file creation failed.
	 * @return
	 */
	public void createXMLFile(String name){
        //File newFile = new File(name);       //Uncomment when add code to detect new file or not
        createDOM();        //Set up the file and let DOM equal the file
	}
	
	
	/**
	 * Save the existing DOM (if it is active) to disk.
	 * Throws exception if file is null.
	 * @return
	 */
	public void saveFile(String name){
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(DOM);
			StreamResult streamResult =  new StreamResult(new File(name)); //EDIT this part to make sure it doesn't have to be a new file
			transformer.transform(source, streamResult);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		xmlStringBuilder.append("<?xml version=\"1.0\"?> <class> CAN YOU READ THIS? </class>");
		ByteArrayInputStream input =  new ByteArrayInputStream(
		   xmlStringBuilder.toString().getBytes("UTF-8"));
		DOM = builder.parse(input);
		
		Element root = DOM.getDocumentElement();
		//System.out.println("BLA: " + root.getAttribute("class"));
		
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
	
	
	
	//Testing
	public static void main(String[] args) {
		System.out.println("TESTING...\n");
		StoreAndRetrieveUnit sru = new StoreAndRetrieveUnit();
		sru.createXMLFile(MAIN_FILE_LOCATION);
		sru.saveFile(MAIN_FILE_LOCATION);
	}

}

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
		if (m == null) throw new IllegalArgumentException("No message received.");
		
		String query = m.getQuery();

		if (query.equals("REGISTER") && m instanceof RegisterMessage){
			return register_new_user((RegisterMessage)m);
		}
		else if (query.equals("LOGIN") && m instanceof LoginMessage) {
			return login_user((LoginMessage)m);
		}
		else if (query.equals("LISTING") && m instanceof ListingMessage) {
			return list_items((ListingMessage)m);
		}
		else if (query.equals("RETRIEVE") && m instanceof RetrieveIdMessage) {
			return retrieve_userID((RetrieveIdMessage)m);
		}
		else if (query.equals("EDIT") && m instanceof EditIdMessage) {
			return edit_userID((EditIdMessage)m);
		}
		else if (query.equals("DELETE") && m instanceof DeleteIdMessage) {
			return delete_userID((DeleteIdMessage)m);
		}
		else if (query.equals("OBLITERATE") && m instanceof ObliterateMessage) {
			return obliterate((ObliterateMessage)m);
		}

		throw new IllegalArgumentException("Invalid message received.");
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
		String uName = reg_m.getUsername();
		String pWord = reg_m.getPassword();

		// Try to register
		String respCode = "OK"; //TODO: Change this!
		logger.log(Level.INFO, "User " + uName + " tried to register");
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to register");
		}

		// Determine and construct Response
		logger.log(Level.INFO, "Created new user");
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
		String uName = log_m.getUsername();
		String pWord = log_m.getPassword();

		// Try to log them in
		String respCode = "OK"; //TODO: Change this!
		logger.log(Level.INFO, "User " + uName + " tried to log in");
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to log in");
		}

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
		String uName = list_m.getUsername();
		String pWord = list_m.getPassword();

		// Try to obtain listing
		String respCode = "OK"; //TODO: Change this!
		// TODO: Each record in listing should contain "id" and "name"
		ArrayList<Record> listing = new ArrayList<Record>(); // TODO: = get Listing from XML
		logger.log(Level.INFO, "User " + uName + " requesting full record listing");
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to retrieve full record listing");
		}

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
		String uName = retr_m.getUsername();
		String pWord = retr_m.getPassword();
		int id = retr_m.getId();

		// Try to obtain record
		String respCode = "OK"; //TODO: Change this!
		Record rec = null; // TODO: = get Record from XML. Should contain "id", "name", "username", "password"
		logger.log(Level.INFO, "User " + uName + " requesting record " + ""); //record's name
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to retrieve record " + id);
		}

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
		String uName = edit_m.getUsername();
		String pWord = edit_m.getPassword();
		int id = edit_m.getId();
		Record updatedRecord = edit_m.getRecord();
		String action = "FILL IN";
		String item = "FIND OUT";

		// Try to modify record
		String respCode = "OK"; //TODO: Change this!
		// TODO: replace existing record of "id"=id with updatedRecord in XML
		logger.log(Level.INFO, "User " + uName + " requests to " + action + " the record corresponding to " + item);
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to edit record " + id);
		}

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
		String uName = del_m.getUsername();
		String pWord = del_m.getPassword();
		int id = del_m.getId();

		// Try to modify record
		String respCode = "OK"; //TODO: Change this!
		// TODO: remove record of "id"=id from XML
		logger.log(Level.INFO, "User " + uName + " requests to PERMANENTLY DELETE record " + id);
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to premanently delete record " + id);
		}

		// Determine and construct Response
		DeleteIdResponse reply = new DeleteIdResponse(uName, pWord, respCode, id);
		logger.log(Level.INFO, "User " + uName + " permanently deleted record " + id);
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
		String uName = obl_m.getUsername();
		String pWord = obl_m.getPassword();

		// Try to modify record
		String respCode = "OK"; //TODO: Change this!
		// TODO: delete all records for this user
		logger.log(Level.WARNING, "User " + uName + " requesting deletion of entire record directory"); // Change to INFO?
		
		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to delete entire record directory");
		}

		// Determine and construct Response
		ObliterateResponse reply = new ObliterateResponse(uName, pWord, respCode);
		logger.log(Level.INFO, "User " + uName + " deleted entire record directory");
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

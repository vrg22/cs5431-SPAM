/* Stores and retrieves all information related to user accounts and data. Writes important information to log.
 * This class is the main point of entry for the server-side communication unit (CommServer.java).
 */

import java.io.*;
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

public class StoreAndRetrieveUnit {
	//XML
	private Document DOM;
	//Storage
	private static final String USERS_FILE_LOCATION = "users.xml";
	private static final int SUCCESS = 0;
	private static final int FAILURE = -1;
	//Logging
	private static final String LOG_FILE_LOCATION = "log.log";
	private static final Logger logger = Logger.getLogger(StoreAndRetrieveUnit.class.getName()); //TODO: Configure

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

		//Create the main users.xml file with the proper setup
		// TODO: Eventually, rather than always having to modify something and save the DOM back to disk on every message,
		// allow to load DOM once and only save it back to disk if ProcessMessage hasn't been called in a while
		if (createMainXMLFile() != SUCCESS) {
			//TODO: complain
		}
	}


	/**
	 * Interprets a Message and returns a Response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	public Message.Response processMessage(Message m) {
		if (m == null) throw new IllegalArgumentException("No message received.");

		//Load main user DOM from XML file
		DOM = loadDOM(USERS_FILE_LOCATION);

		Message.Response reply = null;
		String query = m.getQuery();

		if (query.equals("REGISTER") && m instanceof Message.RegisterMessage){
			reply = register_new_user((Message.RegisterMessage)m);
		}
		else if (query.equals("LOGIN") && m instanceof Message.LoginMessage) {
			reply = login_user((Message.LoginMessage)m);
		}
		else if (query.equals("LISTING") && m instanceof Message.ListingMessage) {
			reply = list_items((Message.ListingMessage)m);
		}
		else if (query.equals("RETRIEVE") && m instanceof Message.RetrieveIdMessage) {
			reply = retrieve_userID((Message.RetrieveIdMessage)m);
		}
		else if (query.equals("EDIT") && m instanceof Message.EditIdMessage) {
			reply = edit_userID((Message.EditIdMessage)m);
		}
		else if (query.equals("DELETE") && m instanceof Message.DeleteIdMessage) {
			reply = delete_userID((Message.DeleteIdMessage)m);
		}
		else if (query.equals("OBLITERATE") && m instanceof Message.ObliterateMessage) {
			reply = obliterate((Message.ObliterateMessage)m);
		}

		//Save DOM to XML file
		saveDOMtoFile(new File(USERS_FILE_LOCATION), DOM); //change so doesn't need FILE passed

		//Return response
		return reply;

		//throw new IllegalArgumentException("Invalid message received.");
	}


	/**
	 * Tries to register a new user and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response register_new_user(Message.RegisterMessage reg_m) {
		// Unpack message, find out who is trying to register
		String uName = reg_m.getUsername();
		String pWord = reg_m.getPassword();

		//the reg_m has uname, need to check if that corresponds to an ID existing on the system
		//if so, then response should send that back along with code saying already registered
		//otherwise, check if there is anyone

		// Try to register
		String respCode = addUser(uName, pWord);
		logger.log(Level.INFO, "User " + uName + " tried to register");

		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to register");
		}

		// Determine and construct Response
		logger.log(Level.INFO, "Created new user");
		Message.RegisterResponse reply = new Message.RegisterResponse(uName, pWord, respCode);
		return reply;
	}

	/**
	 * Tries to login a user and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response login_user(Message.LoginMessage log_m) {
		// Unpack message, find out who is trying to log in
		String uName = log_m.getUsername();
		String pWord = log_m.getPassword();
		int attRem = log_m.getAttemptsRemaining();

		// Try to log them in
		String respCode = loginUser(uName, pWord, attRem);
		logger.log(Level.INFO, "User " + uName + " tried to log in");

		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to log in");
		}

		// Determine and construct Response
		Message.LoginResponse reply = new Message.LoginResponse(uName, pWord, respCode);
		return reply;
	}


	/**
	 * Tries to obtain a full credential listing and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response list_items(Message.ListingMessage list_m) {
		// Unpack message, find out who wants a listing
		String uName = list_m.getUsername();
		String pWord = list_m.getPassword();

		// Try to obtain listing
		// TODO: Each record in listing should contain "id" and "name"
		ArrayList<Message.Record> listing = getListings(uName); //Need pWord here?

		String respCode = null;
		if (listing != null) {
			respCode = "OK";
		} else {
			respCode = "FAILED_UNEXPECTEDERROR";
		}

		logger.log(Level.INFO, "User " + uName + " requesting full record listing");

		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to retrieve full record listing");
		}

		// Determine and construct Response
		Message.ListingResponse reply = new Message.ListingResponse(uName, pWord, respCode, listing);
		return reply;
	}

	/**
	 * Tries to fetch the desired record and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response retrieve_userID(Message.RetrieveIdMessage retr_m) {
		// Unpack message, find out who wants to retrieve a particular record
		String uName = retr_m.getUsername();
		String pWord = retr_m.getPassword();
		int id = retr_m.getId();

		// Try to obtain record
		String respCode = "OK"; //TODO: Change this!
		Message.Record rec = null; // TODO: = get Record from XML. Should contain "id", "name", "username", "password"
		logger.log(Level.INFO, "User " + uName + " requesting record " + ""); //record's name

		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to retrieve record " + id);
		}

		// Determine and construct Response
		Message.RetrieveIdResponse reply = new Message.RetrieveIdResponse(uName, pWord, respCode, id, rec);
		return reply;
	}

	/**
	 * Tries to modify an existing record and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response edit_userID(Message.EditIdMessage edit_m) {
		// Unpack message, find out who wants to edit a particular record
		String uName = edit_m.getUsername();
		String pWord = edit_m.getPassword();
		int id = edit_m.getId();
		Message.Record updatedRecord = edit_m.getRecord();
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
		Message.EditIdResponse reply = new Message.EditIdResponse(uName, pWord, respCode, id);
		return reply;
	}

	/**
	 * Tries to delete an existing record and returns an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response delete_userID(Message.DeleteIdMessage del_m) {
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
		Message.DeleteIdResponse reply = new Message.DeleteIdResponse(uName, pWord, respCode, id);
		logger.log(Level.INFO, "User " + uName + " permanently deleted record " + id);
		return reply;
	}

	/**
	 * Delete's an entire user's records and account and returs an appropriate response.
	 * Throws exception if something unexpected happens.
	 * @return
	 */
	private Message.Response obliterate(Message.ObliterateMessage obl_m) {
		// Unpack message, find out who wants to delete their entire vault
		String uName = obl_m.getUsername();
		String pWord = obl_m.getPassword();

		// Try to modify record
		String respCode = deleteVault(uName);
		// TODO: delete all records for this user
		logger.log(Level.WARNING, "User " + uName + " requesting deletion of entire record directory"); // Change to INFO?

		if (!respCode.equals("OK")) {
			logger.log(Level.WARNING, "User " + uName + " failed to delete entire record directory");
		}

		// Determine and construct Response
		Message.ObliterateResponse reply = new Message.ObliterateResponse(uName, pWord, respCode);
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
	 * Sets up the primary user XML file at USERS_FILE_LOCATION (if it doesn't exist). Does nothing if already exists.
	 * Throws exception if file creation failed.
	 * @return
	 */
	private int createMainXMLFile() {
        File mainFile = new File(USERS_FILE_LOCATION);
		if (!mainFile.exists()) {
	        createMainDOM();        //Instantiates and prepares the DOM to be saved to disk
			saveDOMtoFile(mainFile, DOM); //TODO: Make sure this makes DOM null again
		}

		//TODO: CHANGE THIS!
		return SUCCESS;
	}


	/**
	 * Sets up an XML file (for a single user's vault) with the specified name.
	 * If file already exists, this simply loads its DOM and returns that.
	 * Throws exception if file creation failed.
	 * @return
	 */
	private int createXMLFile(int userID, String username, String password) {
		String filename = Integer.toString(userID) + ".xml";
		//if (filename.equals(USERS_FILE_LOCATION)) throw new Exception();

		File newFile = new File(filename);
		if (!newFile.exists()) {
	        Document userDOM = createUserDOM(userID, username, password);        //Instantiates and prepares the DOM to be saved to disk
			saveDOMtoFile(newFile, userDOM);
		}

		//CHANGE THIS
		return SUCCESS;
	}


	/**
	 * Sets the private field DOM with a Document object with fields specific to the USERS_FILE_LOCATION
	 * Assumes that the private field DOM is not yet set, and overwrites it.
	 * Throws exception if DOM creation failed.
	 * @return
	 */
	private void createMainDOM() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Create XML base and set DOM
			StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?

			//Specific setup for the main XML users file
			setupMainXML(xmlStringBuilder);

			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			DOM = builder.parse(input);
	        DOM.getDocumentElement().normalize();

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


	/**
	 * Sets a blank DOM up with fields specific to a new user.
	 * Throws exception if DOM creation failed.
	 * @return
	 */
	private Document createUserDOM(int ID, String username, String password) {
		Document newDOM = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Create XML base and set DOM
			StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?

			//Specific setup for the main XML users file
			setupUserXML(xmlStringBuilder, ID, username, password);

			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			newDOM = builder.parse(input);
	        newDOM.getDocumentElement().normalize();

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

		return newDOM;
	}


	/**
	 * Load a DOM up from an existing location on the system.
	 * Throws exception if Document creation failed.
	 * @return
	 */
	private Document loadDOM(String fileLoc) {
		Document doc = null;

		try {
			//Load XML from disk and set DOM
			File inputFile = new File(fileLoc);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        doc = dBuilder.parse(inputFile); //TODO: IF FILE DNE, what exception needs to be caught?

			//StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?

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

		return doc;
	}


	/**
	 * Save the provided DOM to disk at the file location.
	 * Throws exception if file is null.
	 * @return //TODO: success code?
	 */
	private void saveDOMtoFile(File file, Document theDOM){
		if (file == null) throw new IllegalArgumentException("No file received.");

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(theDOM);
			StreamResult streamResult =  new StreamResult(file); //EDIT this part to make sure it doesn't have to be a new file
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
	 * Take in a StringBuilder and append to it the bare bones text necessary for the users XML file.
	 */
	private void setupMainXML(StringBuilder sb) {
		//xmlStringBuilder.append("<?xml version=\"1.0\"?>\n<class>\nCAN YOU READ THIS?\n</class>");

		String basicText =
			            "<?xml version=\"1.0\"?>\n"
			            + "<usersXML>\n"
			            + 	"<metadata>\n"
			            + 		"<nextID>0</nextID>\n"
			            + 		"<numUsers>0</numUsers>\n"
			            + 	"</metadata>\n"
			            + 	"<users>\n" //EMPTY RN!
			            + 	"</users>\n"
			            + "</usersXML>"
			            ;

		sb.append(basicText);
	}

	/**
	 * Take in a StringBuilder and append to it the bare bones text necessary for a particular user's XML file.
	 */
	private void setupUserXML(StringBuilder sb, int ID, String username, String password) {
		String newID = Integer.toString(ID);

		String basicText =
			            "<?xml version=\"1.0\"?>\n"
			            + "<user ID=\"" + newID + "\">\n"
			            + 	"<username>" + username + "</username>\n"
			            + 	"<password>" + password + "</password>\n"
			            + 	"<vault></vault>\n" //ADD RECORDS HERE!
			            + "</user>"
			            ;

		sb.append(basicText);
	}


	/**
	 * Adds a user to the main XML file represented by DOM
	 * TODO: Exception handling
	 * @return response code
	 */
	private String addUser(String uname, String pword) {
		String respcode = "OK";

		//Check for too many users existing
		//Directly obtain the "numUsers" element (within the "metadata" tag)
		Element numUElement = getTagElement("numUsers", DOM);
		int numUsers = Integer.parseInt(numUElement.getTextContent());
		if (numUsers == User.MAX_USERS) {
			respcode = "FAILED_MAX_USERS_REACHED";
			return respcode;
		}

		//Check for duplicates already
		for (User u : getUsers()){
			if (u.getUsername().equals(uname)) {
				respcode = "FAILED_EXISTINGUSERNAME";
				return respcode;
			}
		}

		//Directly obtain the "nextID" element (within the "metadata" tag)
		Element idElement = getTagElement("nextID", DOM);
		int nextID = Integer.parseInt(idElement.getTextContent());

		//Add the appropriate tags underneath parent "users" tag
		// Get the element
		// Add child, one row with the three attributes in that order
		Element uElement = getTagElement("users", DOM);

		Element newUser = DOM.createElement("user");
		newUser.setAttribute("ID", Integer.toString(nextID));
		Element usrnm = DOM.createElement("username");
		usrnm.setTextContent(uname);
        newUser.appendChild(usrnm);

        Element pwd = DOM.createElement("password");
		pwd.setTextContent(pword);
        newUser.appendChild(pwd);

        uElement.appendChild(newUser);

		//Create new user's own XML file. Save to disk.
        createXMLFile(nextID, uname, pword);
        Document doc = createUserDOM(nextID, uname, pword);
        saveDOMtoFile(new File(Integer.toString(nextID)+".xml"), doc);

        //Update nextID in main DOM
        nextID++;
		idElement.setTextContent(Integer.toString(nextID));

		//Update numUsers in main DOM
        numUsers++;
		numUElement.setTextContent(Integer.toString(numUsers));

		return respcode;
	}

	/**
	 * Attempt to log a user in (by checking the main XML file in the DOM)
	 * TODO: AUTHENTICATION //For now, we will just "log you in" without checking whether your account actually exists, and catch exceptions arising from actions taken that should not yet exist.
	 * TODO: Exception handling
	 * @return response code
	 */
	private String loginUser(String uName, String pWord, int attRem) {
		String respcode = "OK"; //CHANGE

		//TODO

		return respcode;
	}

	/**
	 * Attempt to delete a user's entire vault into the main XML file represented by DOM
	 * TODO: AUTHENTICATION //For now, we will just "log you in" without checking whether your account actually exists, and catch exceptions arising from actions taken that should not yet exist.
	 * TODO: Exception handling
	 * @return response code
	 */
	private String deleteVault(String uName) {
		String respcode = "FAILED_UNEXPECTEDERROR"; //CHANGE

		String matchstring;
		int id = -1;

		//TODO
		//Already has an account
		//First, delete and get ID
		Element uElement = getTagElement("users", DOM);

		//Iterate through all child nodes of "users" Element until find one that matches the same username
    	for (Node n = uElement.getFirstChild(); n != null; n = n.getNextSibling()) {

    		//Check what the username of this is
    		if (n.getNodeType() == Node.ELEMENT_NODE) {
            	Element uElt = (Element) n;
            	matchstring = uElt.getElementsByTagName("username").item(0).getTextContent();

            	//Check if this is the one
            	if (matchstring.equals(uName)) {
            		//Get ID, delete this Element from the "users" element
            		id = Integer.parseInt(uElt.getAttribute("ID"));
            		uElement.removeChild(uElt); //CHECK!

            		//Delete the file named ID.xml and return
            		File f = new File(Integer.toString(id) + ".xml");
            		if (!f.delete()){
            			//TODO: complain
            		}

            		//Update num of users in metadata of main DOM
            		//Directly obtain the "numUsers" element (within the "metadata" tag), decrement
            		Element numUElement = getTagElement("numUsers", DOM);
            		int numUsers = Integer.parseInt(numUElement.getTextContent());
            		//Update numUsers in main DOM
                    numUsers--;
            		numUElement.setTextContent(Integer.toString(numUsers));

            		respcode = "OK";
            		return respcode;
            	}
    		}
    	}

		return respcode;
	}

	/**
	 * Return ArrayList of current users from loaded DOM. //Or null if no users yet?
	 * TODO: Exception handling
	 * @return
	 */
	private ArrayList<Message.Record> getListings(String uName) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Return ArrayList of current users from loaded main DOM. //Or null if no users yet?
	 * TODO: Exception handling
	 * @return
	 */
	private ArrayList<User> getUsers() {
		//ArrayList of users to be returned
		ArrayList<User> users = new ArrayList<User>();

		//Attributes of each user
		String username;
		String password;
		int id;

		//Get the "users" element
		Element usersElement = getTagElement("users", DOM);

	    //Iterate through all child nodes of "users" Element
    	for (Node n = usersElement.getFirstChild(); n != null; n = n.getNextSibling()) {
        	//System.out.println("\nCurrent Element: " + uNode.getNodeName());

    		//Add this user to our list
    		if (n.getNodeType() == Node.ELEMENT_NODE) {
            	Element uElt = (Element) n;
            	id = Integer.parseInt(uElt.getAttribute("ID"));

            	username = uElt.getElementsByTagName("username").item(0).getTextContent();
            	password = uElt.getElementsByTagName("password").item(0).getTextContent();
            	User u = new User(username, password, id);

            	users.add(u);
            }
    	}

        return users;
	}


	/**
	 * Return Element corresponding to the specified tag. Expects tagname corresponding to exactly one Node in DOM.
	 * TODO: Exception handling
	 * @return
	 */
	private Element getTagElement(String tagname, Document doc) {
	    NodeList nlist = doc.getElementsByTagName(tagname); //TODO: Is it better to get what you want directly by
	    Node n = nlist.item(0);
		Element elt = (Element) n;
		return elt;
	}

	/**
	 * Look at the loaded DOM and print out some info for testing about the main XML file.
	 * TODO: Exception handling
	 */
	private void printDOM() {
		//Verify DOM information
        Element root = DOM.getDocumentElement();
        String rootString = root.getNodeName();
        System.out.println("ROOT ELEMENT: " + rootString);

        //Iterate through Users (check)
        ArrayList<User> userList = getUsers();
        for (User u : userList) {
        	System.out.println("Username: " + u.getUsername());
        }
    }


	//Uncomment for testing
	/*
	 * public static void main(String[] args) {
		System.out.println("TESTING SRU...\n");
		StoreAndRetrieveUnit sru = new StoreAndRetrieveUnit(); //Creates the main XML file at startup IF doesn't already exist

		//Do something
		sru.DOM = sru.loadDOM(USERS_FILE_LOCATION);
		sru.printDOM();
	} */

}
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

// Handles parsing storage files
public class XMLStorageController implements StorageController {

    //TODO: Standardize use of THIS DOM vs an arbitrary DOM in this class
    private Document DOM;

    public void createPasswordsFileOnStream(FileOutputStream out) {
        //Instantiates and prepares the DOM to be saved to disk
        createMainDOM();

        writeDOMtoStream(DOM, out);
    }

    public void createFileForUserOnStream(int userId, FileOutputStream out) {
        Document userDOM = createUserDOM(userId);
        writeDOMtoStream(userDOM, out);
    }

    public PasswordStorageFile readPasswordsFile(FileInputStream in) {
        return DOMtoPasswordsFile(streamToDOM(in));
    }

    public UserStorageFile readFileForUser(FileInputStream in) {
        return DOMtoUserFile(streamToDOM(in));
    }

    public void writeFileToStream(PasswordStorageFile file, FileOutputStream out) {
        writeDOMtoStream(fileToDOM(file), out);
    }

    public void writeFileToStream(UserStorageFile file, FileOutputStream out) {
        writeDOMtoStream(fileToDOM(file), out);
    }

    public String getExtension() {
        return ".xml";
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

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}
	}

    /**
	 * Sets a blank DOM up with fields specific to a new user.
	 * Throws exception if DOM creation failed.
	 * @return
	 */
	private Document createUserDOM(int ID) {
		Document newDOM = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();

			//Create XML base and set DOM
			StringBuilder xmlStringBuilder = new StringBuilder(); //TODO: Make private variable?

			//Specific setup for the main XML users file
			setupUserXML(xmlStringBuilder, ID);

			ByteArrayInputStream input =  new ByteArrayInputStream(xmlStringBuilder.toString().getBytes("UTF-8"));

			newDOM = builder.parse(input);
	        newDOM.getDocumentElement().normalize();

		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
		}

		return newDOM;
	}

    // Populate a Document with the contents of a PasswordStorageFile
    // ASSUMPTION: We can load a "preliminary" DOM from disk, which we expect to have the basic structure
    private Document fileToDOM(PasswordStorageFile file) {
    	    	
    	Document initialDOM, DOM = null;
    	
	    FileInputStream fis = getPasswordsInput();
	    initialDOM = streamToDOM(fis);
    	
    	// Put all entries, etc in the right place
    	DOM = initialDOM;
    	
    	// Set metadata to match
		Element numUElement = getTagElement("numUsers", DOM);
		numUElement.setTextContent(file.getNumUsers());
		Element nextIDElement = getTagElement("nextID", DOM);
		nextIDElement.setTextContent(file.readNextID());
		
		// Make user data match by simply overwriting content
		Element uElement = getTagElement("users", DOM);
		uElement.setTextContent(""); //CHECK!
		
		PasswordStorageEntry pEntry;
		for (StorageEntry entry : file.entries) {
			pEntry = (PasswordStorageEntry) entry;
			Element newUser = DOM.createElement("user");
			newUser.setAttribute("ID", Integer.toString(pEntry.getUserId()));
			Element usrnm = DOM.createElement("username");
			usrnm.setTextContent(pEntry.getUsername());
			newUser.appendChild(usrnm);

	        Element master = DOM.createElement("password");
			master.setTextContent(pEntry.getMaster());
	        newUser.appendChild(master);

	        uElement.appendChild(newUser);
		}
		
    	return DOM;
    }

    // Populate a Document with the contents of a UserStorageFile
    // ASSUMPTION: We can load a "preliminary" DOM from disk, which we expect to have the basic structure
    private Document fileToDOM(UserStorageFile file) {
        // TODO: figure out how to implement this
        return null;
    }

    // Populate a PasswordStorageFile with the contents of a Document
    private PasswordStorageFile DOMtoPasswordsFile(Document theDOM) {
    	
    	PasswordStorageFile file = new PasswordStorageFile();
    	
    	// Set Users and metadata to match file. This will make numUsers correct.
    	// TODO: Need to make nextID variable correct? Or can we just build this into getNextID()?
    	file.setUsers(getUsers(theDOM));
    	
        return file;
    }

    // Populate a UserStorageFile with the contents of a Document
    private UserStorageFile DOMtoUserFile(Document theDOM) {
        // TODO: figure out how to implement this
        return null;
    }

    // Read file from `in` and store it in a Document object
    private Document streamToDOM(FileInputStream in) {
    	
		Document doc = null;

		try {
			//Load XML from disk and set DOM
			//File inputFile = new File(fileLoc);
	        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	        doc = dBuilder.parse(in); //TODO: IF FILE DNE, what exception needs to be caught?

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

		System.out.println("Got here!!");
		
		return doc;
        
    }

    // Convert a Document into file-writable format, write to output stream
    private void writeDOMtoStream(Document theDOM, FileOutputStream out){
		if (out == null) throw new IllegalArgumentException("No output filestream received.");

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(theDOM);
			StreamResult streamResult =  new StreamResult(out);
			transformer.transform(source, streamResult);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }


    //XML File parsing methods
	//At all times, have entirety of the password file in memory???
	//Or is the event-based triggering in SAX more secure?
	//When to do the encryption? Easier/more efficient to encrypt parts of the message or whole thing?

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
	 * Return ArrayList of current users from loaded main DOM. //Or null if no users yet?
	 * TODO: Exception handling
	 * @return
	 */
	private ArrayList<User> getUsers(Document DOM) {
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
	private void setupUserXML(StringBuilder sb, int ID) {
		String newID = Integer.toString(ID);

		String basicText =
			            "<?xml version=\"1.0\"?>\n"
			            + "<user ID=\"" + newID + "\">\n"
			            + 	"<vault></vault>\n" //ADD RECORDS HERE!
			            + "</user>"
			            ;

		sb.append(basicText);
	}
	
	
	
	// Filename access methods
    public String getPasswordsFilename() {
        return PasswordStorageFile.getPasswordsFilename() + getExtension();
    }

    public String getFilenameForUser(int userId) {
        return userId + getExtension();
    }

    public FileInputStream getPasswordsInput() {
        try {
            return new FileInputStream(new File(getPasswordsFilename()));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public FileInputStream getInputForUser(int userId) {
        try {
            return new FileInputStream(new File(getFilenameForUser(userId)));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public FileOutputStream getPasswordsOutput() {
        try {
            return new FileOutputStream(new File(getPasswordsFilename()));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    public FileOutputStream getOutputForUser(int userId) {
        try {
            return new FileOutputStream(new File(getFilenameForUser(userId)));
        } catch (FileNotFoundException e) {
            return null;
        }
    }
}

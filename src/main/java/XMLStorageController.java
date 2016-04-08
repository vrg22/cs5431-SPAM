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

    private static final String USERS_FILE_LOCATION = "users.xml";

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
    private Document fileToDOM(PasswordStorageFile file) {
        // TODO: figure out how to implement this
        return null;
    }

    // Populate a Document with the contents of a UserStorageFile
    private Document fileToDOM(UserStorageFile file) {
        // TODO: figure out how to implement this
        return null;
    }

    // Populate a PasswordStorageFile with the contents of a Document
    private PasswordStorageFile DOMtoPasswordsFile(Document theDOM) {
        // TODO: figure out how to implement this
        return null;
    }

    // Populate a UserStorageFile with the contents of a Document
    private UserStorageFile DOMtoUserFile(Document theDOM) {
        // TODO: figure out how to implement this
        return null;
    }

    // Read file from `in` and store it in a Document object
    private Document streamToDOM(FileInputStream in) {
        // TODO: figure out how to implement this
        return null;
    }

    // Convert a Document into file-writable format, write to output stream
    private void writeDOMtoStream(Document theDOM, FileOutputStream out){
        // TODO: figure out how to implement this
    }


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
}

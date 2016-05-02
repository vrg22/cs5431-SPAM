import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;

//Imported for synchronization of files
import java.util.concurrent.locks.ReentrantReadWriteLock;

//Imported for logging by default logging (java.util.logging)
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Imported for XML parsing by the DOM method
//Adapted from http://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

// Handles parsing storage files
public class XMLStorageController implements StorageController {

    private String passwordFilename;
    //TODO: Standardize use of THIS DOM vs an arbitrary DOM in this class
    private Document DOM;

    private ReentrantReadWriteLock   pwd_lock; //Used to manage reads and writes to users.xml
    private ReentrantReadWriteLock[] user_locks; //Used to manage reads and writes to individual vaults //TODO: Is this necessary?

    public XMLStorageController(String passwordFilename) {
        this.passwordFilename = passwordFilename;

        pwd_lock = new ReentrantReadWriteLock(); //Initialize main RRWL
        user_locks = new ReentrantReadWriteLock[User.MAX_USERS];
      //TODO: Only initialize a particular RRWL for a user when we KNOW that user exists??
        /*
        for (ReentrantReadWriteLock lock : user_locks) {
        	lock = new ReentrantReadWriteLock();
        }
        */
        for (int i=0; i<user_locks.length; i++) {
        	user_locks[i] = new ReentrantReadWriteLock();
        }
    }

    //TODO: Verify Synchronization!
    //TODO: Remove unused methods

    // Read file from disk operations
    public PasswordStorageFile readPasswordsFile(FileInputStream in) {
    	PasswordStorageFile passwordFile;
    	pwd_lock.readLock().lock();
    	passwordFile = DOMtoPasswordsFile(streamToDOM(in));
    	pwd_lock.readLock().unlock();
    	return passwordFile;
    }

    public UserStorageFile readFileForUser(FileInputStream in) {
        return DOMtoUserFile(streamToDOM(in));
    }

    public String getExtension() {
        return ".xml";
    }

    //Write file to disk operations
    //TODO: Ensure getPasswordsOutput() gets executed AFTER fileToDOM
    public void writeFileToDisk(PasswordStorageFile file) {
    	pwd_lock.writeLock().lock();
    	writeDOMtoStream(fileToDOM(file), getPasswordsOutput());
    	pwd_lock.writeLock().unlock();
    }

    //TODO: Remove if unused
    public void writeFileToDisk(UserStorageFile file, int userId) {
    	user_locks[userId].writeLock().lock();
        writeDOMtoStream(fileToDOM(file), getOutputForUser(userId));
    	user_locks[userId].writeLock().unlock();
    }

    public void createPasswordsFileOnStream(FileOutputStream out) {
        //Instantiates and prepares the DOM to be saved to disk
        createMainDOM();
        writeDOMtoStream(DOM, out);

        // Set permissions on passwords file
        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        try {
            Files.setPosixFilePermissions(Paths.get(getPasswordsFilename()), perms);
        } catch (IOException e) {}
    }

    public void createFileForUserOnStream(int userId, FileOutputStream out) {
        Document userDOM = createUserDOM(userId);
        writeDOMtoStream(userDOM, out);

        // Set permissions on vault file
        Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
        perms.add(PosixFilePermission.OWNER_READ);
        perms.add(PosixFilePermission.OWNER_WRITE);
        try {
            Files.setPosixFilePermissions(Paths.get(getFilenameForUser(userId)), perms);
        } catch (IOException e) {}
    }

    public void writeFileToStream(PasswordStorageFile file, FileOutputStream out) {
    	writeDOMtoStream(fileToDOM(file), out);
    }

    public void writeFileToStream(UserStorageFile file, FileOutputStream out) {
        writeDOMtoStream(fileToDOM(file), out);
    }

    public void writeEncryptedUserFileToDisk(int userId, String contents) {
        try {
        	user_locks[userId].writeLock().lock();
            PrintWriter out = new PrintWriter(getFilenameForUser(userId));
            out.println(contents);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
        	//System.out.println("Writelock for " + userId + ": " + user_locks[userId]==null);
        	user_locks[userId].writeLock().unlock();
        }
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

			//Specific setup for a particular user's file
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
		Element numAElement = getTagElement("numAdmins", DOM);
		numAElement.setTextContent(file.getNumAdmins());
		Element nextAdminIDElement = getTagElement("nextAdminID", DOM);
		nextAdminIDElement.setTextContent(file.getNextAdminID());

    	Element numUElement = getTagElement("numUsers", DOM);
		numUElement.setTextContent(file.getNumUsers());
		Element nextUserIDElement = getTagElement("nextUserID", DOM);
		nextUserIDElement.setTextContent(file.getNextUserID());

		// Make user and admin data match by simply overwriting content
		Element uElement = getTagElement("users", DOM);
		uElement.setTextContent(""); //CHECK!

		Element aElement = getTagElement("admins", DOM);
		aElement.setTextContent(""); //CHECK!

		PasswordStorageEntry pEntry;
		for (StorageEntry entry : file.entries) {
			pEntry = (PasswordStorageEntry) entry;

			String type = pEntry.getType();
			if (type.equals("user")) {
				Element newUser = DOM.createElement("user");
				newUser.setAttribute("ID", Integer.toString(pEntry.getId()));

	            Element usrnm = DOM.createElement("username");
				usrnm.setTextContent(pEntry.getUsername());
				newUser.appendChild(usrnm);

		        Element master = DOM.createElement("password");
				master.setTextContent(pEntry.getMaster());
		        newUser.appendChild(master);

	            Element salt = DOM.createElement("salt");
	            salt.setTextContent(CryptoServiceProvider.b64encode(pEntry.getSalt()));
	            newUser.appendChild(salt);

	            Element iv = DOM.createElement("iv");
	            iv.setTextContent(CryptoServiceProvider.b64encode(pEntry.getIV()));
	            newUser.appendChild(iv);

                Element encpass = DOM.createElement("encpass");
    			encpass.setTextContent(pEntry.getEncPass());
    			newUser.appendChild(encpass);

                Element reciv = DOM.createElement("reciv");
                reciv.setTextContent(CryptoServiceProvider.b64encode(pEntry.getRecIV()));
                newUser.appendChild(reciv);

                Element recovery = DOM.createElement("recovery");
                recovery.setTextContent(pEntry.getRecovery());
                newUser.appendChild(recovery);

		        uElement.appendChild(newUser);
			}
			else if (type.equals("admin")) {
				Element newAdmin = DOM.createElement("admin");
				newAdmin.setAttribute("ID", Integer.toString(pEntry.getId()));

	            Element usrnm = DOM.createElement("username");
				usrnm.setTextContent(pEntry.getUsername());
				newAdmin.appendChild(usrnm);

		        Element master = DOM.createElement("password");
				master.setTextContent(pEntry.getMaster());
				newAdmin.appendChild(master);

	            Element salt = DOM.createElement("salt");
	            salt.setTextContent(CryptoServiceProvider.b64encode(pEntry.getSalt()));
	            newAdmin.appendChild(salt);

	            Element iv = DOM.createElement("iv");
	            iv.setTextContent(CryptoServiceProvider.b64encode(pEntry.getIV()));
	            newAdmin.appendChild(iv);

                Element encpass = DOM.createElement("encpass");
    			encpass.setTextContent(pEntry.getEncPass());
    			newAdmin.appendChild(encpass);

                Element reciv = DOM.createElement("reciv");
                reciv.setTextContent(CryptoServiceProvider.b64encode(pEntry.getRecIV()));
                newAdmin.appendChild(reciv);

                Element recovery = DOM.createElement("recovery");
                recovery.setTextContent(pEntry.getRecovery());
                newAdmin.appendChild(recovery);

	            aElement.appendChild(newAdmin);
			}
		}

    	return DOM;
    }

    // Populate a Document with the contents of a UserStorageFile
    // ASSUMPTION: We can load a "preliminary" DOM from disk, which we expect to have the basic structure
    private Document fileToDOM(UserStorageFile file) {

    	Document initialDOM, userDOM = null;

	    // FileInputStream fis = getInputForUser(Integer.parseInt(file.getUserID()));
	    // initialDOM = streamToDOM(fis);
        int userId;
        try {
            userId = Integer.parseInt(file.getUserID());
        } catch (NumberFormatException e) {
            return null;
        }
        initialDOM = createUserDOM(userId);

    	// Put all entries, etc in the right place
    	userDOM = initialDOM;

        // Make vault data match by simply overwriting content
		Element vElement = getTagElement("vault", userDOM);
		vElement.setTextContent(""); //CHECK!

		UserStorageEntry uEntry;
		for (StorageEntry entry : file.entries) {
			uEntry = (UserStorageEntry) entry;
			Element account = userDOM.createElement("account");
			account.setAttribute("ID", Integer.toString(uEntry.getAccountId()));

			Account acc = uEntry.getAccount();

			//TODO: See about making this field optional
			Element name = userDOM.createElement("name");
			name.setTextContent(acc.getName());
			account.appendChild(name);

			Element usrnm = userDOM.createElement("username");
			usrnm.setTextContent(acc.getUsername());
			account.appendChild(usrnm);

			Element pwd = userDOM.createElement("password");
			pwd.setTextContent(acc.getPassword());
			account.appendChild(pwd);

	        vElement.appendChild(account);
		}

    	return userDOM;
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
    	UserStorageFile file = new UserStorageFile(getUserId(theDOM));

    	// Set records to match file
    	file.setRecords(getRecords(theDOM));

        return file;
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

			in.close();

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

    // Convert a Document into file-writable format, write to output stream
    private void writeDOMtoStream(Document theDOM, FileOutputStream out){
		if (out == null) throw new IllegalArgumentException("No output filestream received.");

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer;
		try {
			transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(theDOM);
			StreamResult streamResult = new StreamResult(out);
			transformer.transform(source, streamResult);
			out.close(); //CHECK
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }


    // Convert the provided String (assumed to be well-formed XML) to a Document object
    // Adapted from http://www.java2s.com/Code/Java/XML/ParseanXMLstringUsingDOMandaStringReader.htm
    private Document stringToDOM(String in_XML) {

		Document doc = null;

		try {
	    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(in_XML));

			doc = dBuilder.parse(is);

		} catch (ParserConfigurationException e) {
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

    // Convert the provided Document object to a well-formed XML String
    // Adapted from http://www.java2s.com/Code/Java/XML/ExtractinganXMLformattedstringoutofaDOMobject.htm
    private String writeDOMtoString(Document theDOM) {
    	String resultString = null;

    	TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
    	try{
    		StringWriter writer = new StringWriter();
	        DOMSource source = new DOMSource(theDOM.getDocumentElement());
	        StreamResult result = new StreamResult(writer);
	        transformer = transformerFactory.newTransformer();
	        transformer.transform(source, result);

	        StringBuffer strBuf = writer.getBuffer();
	        resultString = strBuf.toString();

	    } catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return resultString;
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
		byte[] salt;
        byte[] iv;
		int id;
        String encPass;
        String recovery;
        byte[] reciv;
        String twoFactorSecret;

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
            	salt = CryptoServiceProvider.b64decode(
                    uElt.getElementsByTagName("salt").item(0).getTextContent());
                iv = CryptoServiceProvider.b64decode(
                    uElt.getElementsByTagName("iv").item(0).getTextContent());
            	encPass = uElt.getElementsByTagName("encpass").item(0).getTextContent();
            	recovery = uElt.getElementsByTagName("recovery").item(0).getTextContent();
                reciv = CryptoServiceProvider.b64decode(
                    uElt.getElementsByTagName("reciv").item(0).getTextContent());
                twoFactorSecret = uElt.getElementsByTagName("twoFactorSecret").item(0).getTextContent();

            	User u = new User(username, salt, password, id, iv, encPass,
                    reciv, recovery, twoFactorSecret);

            	users.add(u);
            }
    	}

        return users;
	}

	/**
	 * Return ArrayList of accounts from a specified user's DOM. //Or null if no accounts yet?
	 * TODO: Exception handling
	 * @return
	 */
	private ArrayList<Account> getRecords(Document uDOM) {
		//ArrayList of records to be returned
		ArrayList<Account> accts = new ArrayList<Account>();

		//Attributes of each record
		int accId;
		String accName;
		String username;
		String password;

		//Get the "vault" element
		Element vaultElement = getTagElement("vault", uDOM);

	    //Iterate through all child nodes of "vault" Element
    	for (Node n = vaultElement.getFirstChild(); n != null; n = n.getNextSibling()) {

    		//Add this account to our list
    		if (n.getNodeType() == Node.ELEMENT_NODE) {
            	Element accElt = (Element) n;
            	accId = Integer.parseInt(accElt.getAttribute("ID"));

            	accName = accElt.getElementsByTagName("name").item(0).getTextContent();
            	username = accElt.getElementsByTagName("username").item(0).getTextContent();
            	password = accElt.getElementsByTagName("password").item(0).getTextContent();
            	Account a = new Account(accId, accName, username, password);

            	accts.add(a);
            }
    	}

        return accts;
	}

	/**
	 * Return userId from a specified user's DOM.
	 * TODO: Exception handling
	 * @return
	 */
	private int getUserId(Document uDOM) {
		//Metadata
		int userId;

		Element uElement = getTagElement("user", uDOM);
		userId = Integer.parseInt(uElement.getAttribute("ID"));

		return userId;
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
			            + 		"<nextAdminID>0</nextAdminID>\n"
			            + 		"<numAdmins>0</numAdmins>\n"
			            + 		"<nextUserID>0</nextUserID>\n"
			            + 		"<numUsers>0</numUsers>\n"
			            + 	"</metadata>\n"
			            + 	"<users>\n" //EMPTY RN!
			            + 	"</users>\n"
			            + 	"<admins>\n" //EMPTY RN!
			            + 	"</admins>\n"
			            + "</usersXML>"
			            ;

		sb.append(basicText);
	}

    /**
	 * Take in a StringBuilder and append to it the bare bones text necessary for a particular user's XML file.
	 */
	public void setupUserXML(StringBuilder sb, int ID) {
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
        return passwordFilename + getExtension();
    }

    public String getFilenameForUser(int userId) {
        return passwordFilename + "-" + userId + getExtension() + ".enc";
    }

    // Methods for getting file stream objects to work with
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

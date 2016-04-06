public class XMLServerStorageController implements ServerStorageController {

    private static final String USERS_FILE_LOCATION = "users.xml";

    private Document DOM;

    public void createPasswordsFile() {
        File mainFile = new File(USERS_FILE_LOCATION);
        if (!mainFile.exists()) {
            //Instantiates and prepares the DOM to be saved to disk
            createMainDOM();

            //TODO: Make sure this makes DOM null again
            saveDOMtoFile(mainFile, DOM);
        }
    }

    public void createFileForUser(int userId) {
        String filename = Integer.toString(userId) + ".xml";

        File newFile = new File(filename);
        if (!newFile.exists()) {
            Document userDOM = createUserDOM(userId);
            saveDOMtoFile(newFile, userDOM);
        }
    }

    public PasswordStorageFile getPasswordsFile() {

    }

    public UserStorageFile getFileForUser(int userId) {

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

		} catch (ParserConfigurationException | UnsupportedEncodingException
                | SAXException | IOException e) {
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

		} catch (ParserConfigurationException | UnsupportedEncodingException
                | SAXException | IOException e) {
			e.printStackTrace();
		}

		return newDOM;
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
			StreamResult streamResult =  new StreamResult(file); //TODO: EDIT this part to make sure it doesn't have to be a new file
			transformer.transform(source, streamResult);
		} catch (TransformerConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
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

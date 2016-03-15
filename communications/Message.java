package communications;
/*
 * Messages passed between client and server over the network
 */

import java.io.Serializable;

public class Message implements Serializable {
	private static final int globalVersion = 0;
	private static int globalSequence;
	private static final long serialVersionUID = 1L;
	/**
	 * List of query codes:
	 * REGISTER
	 * LOGIN
	 * GETLIST
	 * RETREIVE
	 * EDIT
	 * DELETE
	 * OBLITERATE
	 */
	private String query = null;
	/**
	 * List of response codes:
	 * OK
	 * FAILED_EXISTINGUSERNAME
	 * FAILED_INCORRECT_CREDENTIALS
	 * FAILED_MAX_RECORDS_REACHED
	 */
	private String response = null;
	private int sequence;
	private int version;
	private String username = null;
	private String password = null;

	protected Message() {
		System.err.println("Unused");
	}

	protected Message(String query, String uName, String pWord) {
		this.version = globalVersion;
		this.sequence = globalSequence++;
		this.query = query;
		this.username = uName;
		this.password = pWord;
	}

	static int getGlobalVersion() {
		return globalVersion;
	}

	static int getGlobalSequence() {
		return globalSequence;
	}

	public String getQuery() {
		return query;
	}

	public int getVersion() {
		return this.version;
	}

	public int getSequence() {
		return this.sequence;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getResponse() {
		return this.response;
	}

	public void updateUsername(String newUname) {
		this.username = newUname;
	}

	public void updatePassword(String newPass) {
		this.password = newPass;
	}

	public void updateResponse(String resp) {
		this.response = resp;
	}
	
	
	public static class RegisterMessage extends Message {
		private RegisterMessage() {
			super();
			System.err.println("Unused");
		}

		public RegisterMessage(String uName, String pWord) {
			super("REGISTER", uName, pWord);
		}
	}


	public static class LoginMessage extends Message {
		private int attemptsRemaining = 0;

		private LoginMessage() {
			super();
			System.err.println("Unused");
		}

		public LoginMessage(String uName, String pWord) {
			super("LOGIN", uName, pWord);
			this.attemptsRemaining = 3;
		}

		public void failedAttempt() {
			--this.attemptsRemaining;
		}
	}


	public static class ListingMessage extends Message {
		/**
		 * TODO: Structure of the returned listing message?
		 */

		private ListingMessage() {
			super();
			System.err.println("Unused");
		}

		public ListingMessage(String uName, String pWord) {
			super("LISTING", uName, pWord);
		}
	}


	public static class RetreiveIdMessage extends Message {
		private int id;
		/**
		 * TODO: How do we represent one record?
		 */

		private RetreiveIdMessage() {
			super();
			System.err.println("Unused");
		}

		public RetreiveIdMessage(String uName, String pWord, int id) {
			super("RETREIVE", uName, pWord);
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}
	}


	public static class EditIdMessage extends Message {
		private int id;
		/**
		 * TODO: How do we represent one record?
		 */

		private EditIdMessage() {
			super();
			System.err.println("Unused");
		}

		public EditIdMessage(String uName, String pWord, int id) {
			super("EDIT", uName, pWord);
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}
	}


	public static class DeleteIdMessage extends Message {
		private int id;

		private DeleteIdMessage() {
			super();
			System.err.println("Unused");
		}

		public DeleteIdMessage(String uName, String pWord, int id) {
			super("DELETE", uName, pWord);
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}
	}


	public static class ObliterateMessage extends Message {
		private ObliterateMessage() {
			super();
			System.err.println("Unused");
		}

		public ObliterateMessage(String uName, String pWord) {
			super("OBLITERATE", uName, pWord);
		}
	}
}
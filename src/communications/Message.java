package communications;

/*
 * Messages passed between client and server over the network
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	private static final int globalVersion = 0;
	private static int globalSequence;
	private static final long serialVersionUID = 1L;
	/**
	 * List of query codes:
	 * REGISTER
	 * LOGIN
	 * LISTING
	 * RETRIEVE
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
	protected String responseCode = null;
	protected ArrayList<Record> responseRecords = null;
	private int sequence;
	private int version;
	private String username = null;
	private String password = null;

	protected Message() {
		System.err.println("Unused");
	}

	protected Message(String query, String uName, String pWord) {
		this.version = globalVersion;
		synchronized(Message.class) {
			this.sequence = globalSequence++;
		}
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

	public String getResponseCode() {
		return this.responseCode;
	}

	public ArrayList<Record> getResponseRecords() {
		return this.responseRecords;
	}

	public void updateUsername(String newUname) {
		this.username = newUname;
	}

	public void updatePassword(String newPass) {
		this.password = newPass;
	}

	public void updateResponseCode(String code) {
		this.responseCode = code;
	}

	public void updateResponseRecords(ArrayList<Record> records) {
		this.responseRecords = records;
	}

	public static class KeyValue implements Serializable {
		private static final long serialVersionUID = 1L;
		
		public String key;
		public String value;

		public KeyValue(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}

	public static class Record implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private ArrayList<KeyValue> fields;

		public Record() {
			fields = new ArrayList<>();
		}

		public void addField(KeyValue field) {
			fields.add(field);
		}

		public ArrayList<KeyValue> getFields() {
			return fields;
		}

		public String get(String key) {
			for (KeyValue field : fields) {
				if (field.key.equals(key)) {
					return field.value;
				}
			}
			return null;
		}
		
		public boolean contains(String key) {
			for (KeyValue field : fields) {
				if (field.key.equals(key)) {
					return true;
				}
			}
			return false;
		}
	}

	public static class Response extends Message {
		private static final long serialVersionUID = 1L;
		
		public Response(String query, String uName, String pWord, String respCode) {
			super(query, uName, pWord);
			this.responseCode = respCode;
		}

		public Response(String query, String uName, String pWord, String respCode, ArrayList<Record> records) {
			super(query, uName, pWord);
			this.responseCode = respCode;
			this.responseRecords = records;
		}
	}


	public static class RegisterMessage extends Message {
		private static final long serialVersionUID = 1L;
		
		public RegisterMessage(String uName, String pWord) {
			super("REGISTER", uName, pWord);
		}
	}

	public static class RegisterResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		public RegisterResponse(String uName, String pWord, String respCode) {
			super("REGISTER", uName, pWord, respCode);
		}
	}


	public static class LoginMessage extends Message {
		private static final long serialVersionUID = 1L;
		
		private int attemptsRemaining = 0;

		public LoginMessage(String uName, String pWord) {
			super("LOGIN", uName, pWord);
			this.attemptsRemaining = 3;
		}

		public void failedAttempt() {
			--this.attemptsRemaining;
		}

		public int getAttemptsRemaining() {
			return this.attemptsRemaining;
		}
	}

	public static class LoginResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		public LoginResponse(String uName, String pWord, String respCode) {
			super("LOGIN", uName, pWord, respCode);
		}
	}


	public static class ListingMessage extends Message {
		private static final long serialVersionUID = 1L;
		
		public ListingMessage(String uName, String pWord) {
			super("LISTING", uName, pWord);
		}
	}

	public static class ListingResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		public ListingResponse(String uName, String pWord, String respCode, ArrayList<Record> records) {
			super("LISTING", uName, pWord, respCode, records);
		}
	}


	public static class RetrieveIdMessage extends Message {
		private static final long serialVersionUID = 1L;
		
		private int id;

		public RetrieveIdMessage(String uName, String pWord, int id) {
			super("RETRIEVE", uName, pWord);
			this.id = id;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}
	}

	public static class RetrieveIdResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		private int id;

		public RetrieveIdResponse(String uName, String pWord, String respCode, int id, Record rec) {
			super("RETRIEVE", uName, pWord, respCode);

			this.id = id;

			ArrayList<Record> record = new ArrayList<Record>();
			record.add(rec);
			this.responseRecords = record;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}

		public Record getRecord() {
			ArrayList<Record> records = this.responseRecords;
			if (records == null || records.size() == 0) return null;
			return records.get(0);
		}
	}


	public static class EditIdMessage extends Message {
		private static final long serialVersionUID = 1L;
		
		private int id;
		private Record record;

		public EditIdMessage(String uName, String pWord, int id, Record rec) {
			super("EDIT", uName, pWord);
			this.id = id;
			this.record = rec;
		}

		public int getId() {
			return this.id;
		}

		public void updateId(int newId) {
			this.id = newId;
		}

		public Record getRecord() {
			return this.record;
		}

		public void updateRecord(Record newRec) {
			this.record = newRec;
		}
	}

	public static class EditIdResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		private int id;

		public EditIdResponse(String uName, String pWord, String respCode, int id) {
			super("EDIT", uName, pWord, respCode);
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
		private static final long serialVersionUID = 1L;
		
		private int id;

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

	public static class DeleteIdResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		private int id;

		public DeleteIdResponse(String uName, String pWord, String respCode, int id) {
			super("DELETE", uName, pWord, respCode);
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
		private static final long serialVersionUID = 1L;
		
		public ObliterateMessage(String uName, String pWord) {
			super("OBLITERATE", uName, pWord);
		}
	}

	public static class ObliterateResponse extends Response {
		private static final long serialVersionUID = 1L;
		
		public ObliterateResponse(String uName, String pWord, String respCode) {
			super("OBLITERATE", uName, pWord, respCode);
		}
	}
}

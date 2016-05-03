import java.util.ArrayList;
import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class PasswordStrength {
  private static ArrayList<String> dictWords;
  private static ArrayList<String> myspaceWords;
  private static HashMap<String, String> leetSpeakToPlainTextMap;

  static {
	try {
	  BufferedReader bufferedReader = new BufferedReader(new FileReader("unixdictwords.txt"));
	  String blackListed = bufferedReader.readLine();
	  dictWords = new ArrayList<String>();
	  do {
		dictWords.add(blackListed);
		blackListed = bufferedReader.readLine();
	  } while (blackListed != null);

	  bufferedReader = new BufferedReader(new FileReader("myspace.txt"));
	  blackListed = bufferedReader.readLine();
	  myspaceWords = new ArrayList<String>();
	  do {
		myspaceWords.add(blackListed);
		blackListed = bufferedReader.readLine();
	  } while (blackListed != null);

	  createLeetSpeakMap();
	} catch (FileNotFoundException e) {
	  System.err.println("No such file");
	} catch (IOException e) {
	  System.err.println("Garden variety IOException");
	}
  }

  private static void createLeetSpeakMap() {
	leetSpeakToPlainTextMap = new HashMap<String, String>();

	//Credit: List of leetspeak of characters from Stackoverflow
	leetSpeakToPlainTextMap.put("4", "A");
	leetSpeakToPlainTextMap.put("/"+"\\","A");
	leetSpeakToPlainTextMap.put("@", "A");
	leetSpeakToPlainTextMap.put("^", "A");

	leetSpeakToPlainTextMap.put("13", "B");
	leetSpeakToPlainTextMap.put("/3", "B");
	leetSpeakToPlainTextMap.put("|3", "B");
	leetSpeakToPlainTextMap.put("8", "B");

	leetSpeakToPlainTextMap.put("><", "X");

	leetSpeakToPlainTextMap.put("<", "C");
	leetSpeakToPlainTextMap.put("(", "C");

	leetSpeakToPlainTextMap.put("|)", "D");
	leetSpeakToPlainTextMap.put("|>", "D");

	leetSpeakToPlainTextMap.put("3", "E");

	leetSpeakToPlainTextMap.put("6", "G");

	leetSpeakToPlainTextMap.put("/-/", "H");
	leetSpeakToPlainTextMap.put("[-]", "H");
	leetSpeakToPlainTextMap.put("]-[", "H");

	leetSpeakToPlainTextMap.put("!", "I");

	leetSpeakToPlainTextMap.put("|_", "L");

	leetSpeakToPlainTextMap.put("_/", "J");
	leetSpeakToPlainTextMap.put("_|", "J");
	leetSpeakToPlainTextMap.put("|\\/|", "M");
	leetSpeakToPlainTextMap.put("|\\|", "N");
	leetSpeakToPlainTextMap.put("1", "L");

	leetSpeakToPlainTextMap.put("0", "O");

	leetSpeakToPlainTextMap.put("5", "S");

	leetSpeakToPlainTextMap.put("7", "T");
	leetSpeakToPlainTextMap.put("|_|", "U");
	leetSpeakToPlainTextMap.put("\\/\\/", "W");
	leetSpeakToPlainTextMap.put("\\/", "V");
	leetSpeakToPlainTextMap.put("2", "Z");
  }

  private static String getPlainText(String password) {
	Set<String> keySet = leetSpeakToPlainTextMap.keySet();
	String plainText = password;
	// Replace all leet speak character sequences by corresponding english letters 
	for(String key : keySet) {
	  plainText = plainText.replace(key, leetSpeakToPlainTextMap.get(key));
	}
	return plainText;
  }

  private static boolean checkBannedWords(String password) {
	password = password.toLowerCase();
	password = getPlainText(password);
	for (String word : dictWords) {
	  if (word.length() > 4 && password.contains(word)) {
		return false;
	  }
	}

	for (String word : myspaceWords) {
	  if (word.length() > 4 && password.contains(word)) {
		return false;
	  }
	}

	return true;
  }

  private static boolean checkEntropy(String password) {
	boolean ret = true;
	boolean specialCharacterPresent = false;

	String specialCharacters = "!@#$%^&*()_";
	String lower = password.toLowerCase();
	ret = ret && (!password.equals(lower));
	String upper = password.toUpperCase();
	ret = ret && (!password.equals(upper));
	ret = ret && password.matches(".*\\d+.*");

	for (int i = 0; i < specialCharacters.length(); i++) {
	  if (password.indexOf(specialCharacters.charAt(i)) > 0) {
		specialCharacterPresent = true;
		break;
	  }
	}

	return (ret && specialCharacterPresent);
  }

  public static boolean check(String password) {
	boolean ret = false;
	if (password.length() >= 10) {
	  if (checkEntropy(password) && checkBannedWords(password)) {
		ret = true;
	  }
	}
	return ret;
  }
}

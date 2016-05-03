public class PasswordStrength {
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
	if (password.length() >= 12) {
	  if (checkEntropy(password)) {
		ret = true;
	  }
	}
	return ret;
  }
}

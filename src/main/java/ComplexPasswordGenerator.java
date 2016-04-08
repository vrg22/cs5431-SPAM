package main.java;

import java.security.SecureRandom;

public class ComplexPasswordGenerator implements PasswordGenerator {
  private SecureRandom random = new SecureRandom();

  private char[] alphabet = new char[] {
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
      'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G',
      'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
      'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2',
      '3', '4', '5', '6', '7', '8', '9', '!', '@', '#', '$',
      '%', '^', '&', '*', ';', '?'
  };

  public String next(int length) {
      StringBuilder b = new StringBuilder();
      for (int i = 0; i < length; i++) {
          b.append(alphabet[random.nextInt(alphabet.length)]);
      }
      return b.toString();
  }
}

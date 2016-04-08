package main.java;

import java.security.SecureRandom;

public interface PasswordGenerator {
  public String next(int length);
}

import java.util.*;

public class AuthenticationKey {

    private static final int MINUTES_TO_EXPIRE = 15;

    private String key;
    private GregorianCalendar expiration;

    public AuthenticationKey(String key) {
        this.key = key;

        GregorianCalendar expiration = new GregorianCalendar();
        expiration.add(Calendar.MINUTE, MINUTES_TO_EXPIRE);
        this.expiration = expiration;
    }

    /**
     * @return "This key matches the specified authentication key"
     */
    public boolean matches(String keyToCheck) {
        return key.equals(keyToCheck);
    }

    /**
     * @return "This key has expired"
     */
    public boolean hasExpired() {
        GregorianCalendar now = new GregorianCalendar();
        return now.after(expiration);
    }
}

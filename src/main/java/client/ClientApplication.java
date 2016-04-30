import com.google.gson.Gson;

public abstract class ClientApplication
{
	public static final String HTTPS_ROOT = "https://ec2-52-91-199-182.compute-1.amazonaws.com:4567";	
	protected static final int PASSWORD_LENGTH = 12;
    protected Gson gson;

	/**
	 * Attempt to log in with specified credentials
	 *
	 * @return Was login successful
	 */
	public abstract boolean login(String email, String password);

    public abstract void logout();

    /**
     * Register new user, and log in with the new user.
     *
     * @return Was user successfully registered
     */
    public abstract boolean register(String email, String password);

    /**
     * Generate a random password
     *
     * @return a random password
     */
    public String generatePassword() {
        return new ComplexPasswordGenerator().next(PASSWORD_LENGTH);
    }

}

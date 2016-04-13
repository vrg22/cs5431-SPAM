import org.junit.*;
import static org.junit.Assert.*;

public class CryptoServiceProviderTest {
	@Test
		public void testSaltHash() {
			CryptoServiceProvider csp = new CryptoServiceProvider();
			byte[] salt = csp.getNewSalt();
			String salted = csp.genSaltedHash("supersecretpassword", salt);
		}

	@Test
		public void testEncryption() {
			CryptoServiceProvider csp = new CryptoServiceProvider();
			byte[] salt = csp.getNewSalt();

			String data = "Secret Data";
			String encrypted = csp.encrypt(data, "supersecretpassword", salt);
			byte[] iv = csp.getIV();

			String decrypted = csp.decrypt(encrypted, "supersecretpassword", salt, iv);

			assertEquals(data, decrypted);
		}
}

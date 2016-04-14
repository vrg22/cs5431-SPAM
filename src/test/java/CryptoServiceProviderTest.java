import org.junit.*;
import static org.junit.Assert.*;

public class CryptoServiceProviderTest {
	@Test
		public void testSaltHash() {
			byte[] salt = CryptoServiceProvider.getNewSalt();
			String salted = CryptoServiceProvider.genSaltedHash("supersecretpassword", salt);
			String salted_new = CryptoServiceProvider.genSaltedHash("supersecretpassword", salt);

			assertEquals(salted, salted_new);
		}

	@Test
		public void testEncryption() {
			byte[] salt = CryptoServiceProvider.getNewSalt();
			String data = "Secret Data";
			String encrypted = CryptoServiceProvider.encrypt(data, "supersecretpassword", salt);
			byte[] iv = CryptoServiceProvider.getIV();
			String decrypted = CryptoServiceProvider.decrypt(encrypted, "supersecretpassword", salt, iv);

        assertEquals(data, decrypted);
    }
}

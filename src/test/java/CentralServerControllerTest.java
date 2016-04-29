import org.junit.*;
import java.io.*;
import static org.junit.Assert.*;
import static spark.Spark.*;

public class CentralServerControllerTest {
    @Test
    public void testSingleUser() {
        ServerController server = null;
        try {
            server = new CentralServerController("tests.log", "testusers");
        } catch (IOException e) {
            fail("IOException opening log file");
            return;
        }

        // Register new user
        PasswordStorageFile passwordFile = new PasswordStorageFile();
        User registered = server.registerNewUser("Bob",
            CryptoServiceProvider.b64encode(new byte[64]), "saltedhash",
            "encryptedvault", CryptoServiceProvider.b64encode(new byte[64]),
            "0.0.0.0", passwordFile, "encryptedpass", CryptoServiceProvider.
            b64encode(new byte[64]), "recoverypass",
            CryptoServiceProvider.getNewTwoFactorSecretKey());
        assertNotNull(registered);

        int registeredId = registered.getId();
        assertFalse(registeredId == -1);

        assertTrue(passwordFile.containsUsername("user", "Bob"));

        // Obliterate registered user
        assertTrue(server.obliterateUser(registeredId, "0.0.0.0", passwordFile));
        assertFalse(passwordFile.containsUsername("user", "Bob"));

        // Register new user with same username as obliterated username
        registered = server.registerNewUser("Bob",
            CryptoServiceProvider.b64encode(new byte[64]), "newsaltedhash",
            "newencryptedvault", CryptoServiceProvider.b64encode(new byte[64]),
            "0.0.0.0", passwordFile, "encryptedpass", CryptoServiceProvider.
            b64encode(new byte[64]), "recoverypass",
            CryptoServiceProvider.getNewTwoFactorSecretKey());
        assertNotNull(registered);

        registeredId = registered.getId();
        assertFalse(registeredId == -1);

        assertTrue(passwordFile.containsUsername("user", "Bob"));

        // TODO: test updateUser(), getAccountsForUser(),
        //      getDetailsForAccount(), storeNewAccountForUser(),
        //      updateAccount(), deleteAccount(),
        //      & isAccountForUser()
    }
}

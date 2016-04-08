import org.junit.*;
import static org.junit.Assert.*;

import static spark.Spark.*;

public class CentralServerControllerTest {
    // TODO: support a separate storage system for testing

    @Test
    public void testSingleUser() {
        ServerController server = new CentralServerController();

        // Register new user
        User registered = server.registerNewUser("Bob", "supersecretpassword");
        assertNotNull(registered);

        int registeredId = registered.getID();
        assertFalse(registeredId == -1);

        // Login with registered user
        assertEquals(registeredId, server.login("Bob", "supersecretpassword"));

        // Obliterate registered user
        assertTrue(server.obliterateUser(registeredId));

        // Login with obliterated user
        assertEquals(-1, server.login("Bob", "supersecretpassword"));

        // Register new user with same username as obliterated username
        registered = server.registerNewUser("Bob", "newpassword");
        assertNotNull(registered);

        registeredId = registered.getID();
        assertFalse(registeredId == -1);

        // Login with re-registered username, new password
        assertEquals(registeredId, server.login("Bob", "newpassword"));

        // Login with re-registered username, old passwordFile
        assertEquals(-1, server.login("Bob", "supersecretpassword"));

        // TODO: test updateUser(), getAccountsForUser(),
        //      getDetailsForAccount(), storeNewAccountForUser(),
        //      updateAccount(), deleteAccount(),
        //      & isAccountForUser()
    }
}

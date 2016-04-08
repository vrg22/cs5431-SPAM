import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

import static spark.Spark.*;

public class XMLStorageControllerTest {
    @Test
        // TODO: implement
    }

    @Test
    public void testCreateFileForUserOnStream() {
        // TODO: implement
    }

    @Test
    public void testReadEmptyPasswordsFile() {
        PasswordStorageFile expected = new PasswordStorageFile();

        XMLStorageController store = new XMLStorageController();
        String testFile =
            "<?xml version=\"1.0\"?>\n"
            + "<usersXML>\n"
            +   "<metadata>\n"
            +       "<nextID>0</nextID>\n"
            +       "<numUsers>0</numUsers>\n"
            +   "</metadata>\n"
            +   "<users>\n"
            +   "</users>\n"
            + "</usersXML>"
            ;
        InputStream in = new ByteArrayInputStream(
            testFile.getBytes(StandardCharsets.UTF_8));
        PasswordStorageFile actual = store.readPasswordsFile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testReadPopulatedPasswordsFile() {
        PasswordStorageFile expected = new PasswordStorageFile();
        User user1 = new User("user1", "password1", 0);
        User user2 = new User("user2", "password2", 1);
        User user3 = new User("user3", "password3", 2);

        XMLStorageController store = new XMLStorageController();
        String testFile =
            "<?xml version=\"1.0\"?>\n"
            + "<usersXML>\n"
            +   "<metadata>\n"
            +       "<nextID>3</nextID>\n"
            +       "<numUsers>3</numUsers>\n"
            +   "</metadata>\n"
            +   "<users>\n"
            +       "<user ID=\"0\">\n"
            +           "<username>user1</username>\n"
            +           "<password>password1</password>\n"
            +       "</user>\n"
            +       "<user ID=\"1\">\n"
            +           "<username>user2</username>\n"
            +           "<password>password2</password>\n"
            +       "</user>\n"
            +       "<user ID=\"2\">\n"
            +           "<username>user3</username>\n"
            +           "<password>password3</password>\n"
            +       "</user>\n"
            +   "</users>\n"
            + "</usersXML>"
            ;
        InputStream in = new ByteArrayInputStream(
            testFile.getBytes(StandardCharsets.UTF_8));
        PasswordStorageFile actual = store.readPasswordsFile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testReadEmptyFileForUser() {
        UserStorageFile expected = new UserStorageFile();

        XMLStorageController store = new XMLStorageController();
        String testFile =
            "<?xml version=\"1.0\"?>\n"
            + "<user ID=\"82\">\n"
            +   "<username>bob@gmail.com</username>\n"
            +   "<password>supersecretpassword</password>\n"
            +   "<vault></vault>\n"
            + "</user>"
            ;
        InputStream in = new ByteArrayInputStream(
            testFile.getBytes(StandardCharsets.UTF_8));
        PasswordStorageFile actual = store.readPasswordsFile(in);

        assertEquals(expected, actual);
    }

    @Test
    public void testReadPopulatedFileForUser() {
        // TODO: implement
    }

    @Test
    public void testWritePasswordFileToStream() {
        // TODO: implement
    }

    @Test
    public void testWriteUserFileOnStream() {
        // TODO: implement
    }
}

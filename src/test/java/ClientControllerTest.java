import org.junit.*;
import static org.junit.Assert.*;

import static spark.Spark.*;

public class ClientControllerTest {
    @BeforeClass
    public static void beforeClass() {
        Main.main(null);
    }

    @AfterClass
    public static void afterClass() {
        stop();
    }

    // TODO: add test methods
    // @Test
    // public void aTestMethod() {
    //     ...
    // }
}

import org.junit.*;
import static org.junit.Assert.*;

import static spark.Spark.*;

public class FunctionalTest {
    @BeforeClass
    public static void beforeClass() {
        Main.main(null); // Start server
    }

    @AfterClass
    public static void afterClass() {
        stop(); // Stop server
    }

    // TODO: add test methods
    // @Test
    // public void aTestMethod() {
    //     ...
    // }
}

import org.junit.*;
import static org.junit.Assert.*;

public class ComplexPasswordGeneratorTest {
    @Test
    public void testCreate() {
        PasswordGenerator complex = new ComplexPasswordGenerator();

        for (int i = 0; i < 1000; i++) {
            String pw = complex.next(i);
            assertNotNull(pw);
            assertEquals(i, pw.length());
        }
    }
}

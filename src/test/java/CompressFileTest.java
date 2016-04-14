import org.junit.*;
import static org.junit.Assert.*;
import java.io.File;

public class CompressFileTest {
	@Test
		public void compressFileTest() {
			String zFile = "test.zip";
			String file = "test.txt";

			File f = new File(zFile);

			if (f.delete()) {
				System.out.println("Deleted Existing zip file:" +zFile);
			}

			CompressFile.createzipFile(file, zFile);

			if (f.delete()) {
				System.out.println("Deleting compressed file:" +zFile);
			}
		}
}

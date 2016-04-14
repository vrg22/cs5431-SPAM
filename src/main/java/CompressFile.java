import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CompressFile {
	public static void createzipFile(String fileName, String zipFileName) {
		byte[] buffer = new byte[1024];

		try {
			FileOutputStream fos = new FileOutputStream(zipFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			ZipEntry ze = new ZipEntry(fileName);

			zos.putNextEntry(ze);

			FileInputStream in = new FileInputStream(fileName);

			int len;
			while ((len = in.read(buffer)) > 0) {
				zos.write(buffer, 0, len);
			}

			in.close();
			zos.closeEntry();
			zos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			System.err.println(ex.getMessage());
		}
	}
}

import java.util.*;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermission;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class Main {
    private static String adminpassphrase;
    private static String logLocation;
    private static String systemConfigFile;
    private static byte[] salt = new byte[16];
    private static byte[] iv = new byte[16];

    public static String b64ToFilename(String b64) {
        b64 = b64.replace('/', '_');
        b64 = b64.replace('+', '-');

        return b64;
    }

	public static String getAdminPassword() {
		return adminpassphrase;
	}

    public static String filenameToB64(String fname){
        fname = fname.replace('_', '/');
        fname = fname.replace('-', '+');

        return fname;
    }

    public static byte[] getSystemSalt() {
        return salt;
    }

    public static byte[] getSystemIV() {
        return iv;
    }

    public static void setSystemIV(byte[] newIV) {
        iv = newIV;
    }

    private static Boolean startupRoutine() {
        // Retreive admin salt from a file, if absent generate and write to file
        File file = new File(systemConfigFile);
        Boolean ret = false;
        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String sa = br.readLine();
            System.out.println("Read system salt:" +sa);
            salt = CryptoServiceProvider.b64decode(sa);
            String ivString = br.readLine();

            if (ivString != null) {
                System.out.println("Read System IV:" + ivString);
            } else {
                System.err.println("IV is not present in file");
            }

            br.close();
            fr.close();
            ret = true;
        } catch (FileNotFoundException fne) {
            System.out.println("salt file not found, creating");
            try {
                PrintWriter pw = new PrintWriter(file);
                salt = CryptoServiceProvider.getNewSalt();
                String saltString = CryptoServiceProvider.b64encode(salt);
                System.out.println("Generated system salt:" +saltString);
                pw.println(saltString);
                pw.close();
                ret = true;
            } catch (IOException e) {
                System.err.println("Error creating config file" + e.getMessage());
            }
        } catch (IOException e) {
            System.err.println("Error reading config file" + e.getMessage());
        }

        return ret;
    }


    public static void main(String[] args) {
        String logFileName = new SimpleDateFormat("yyyyMMddhhmm'.log'").
            format(new Date());
        adminpassphrase = (args != null && args.length > 0)
            ? args[0]
            : "defaultadminpassforencryption";
        String passwordsLocation = (args != null && args.length > 1)
            ? args[1]
            : "users";
        systemConfigFile = (args != null && args.length > 2)
            ? args[2]
            : "systemconfig.txt";
        logLocation = (args != null && args.length > 3)
            ? args[3]
            : logFileName;

        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run() {
                System.out.println("Shutdown Hook: adminpassphrase -- " + adminpassphrase);
                System.out.println("Shutdown Hook: logLocation -- " + logLocation);

                try {
                    File file = new File(logLocation);
                    FileInputStream fis = new FileInputStream(file);
                    byte[] data = new byte[(int) file.length()];
                    fis.read(data);
                    fis.close();
                    String str = new String(data, "UTF-8");
                    System.out.println("Shutdown Hook: str -- " + str);
                    String encrypted = CryptoServiceProvider.encrypt(str, adminpassphrase,
                                        salt);
                    String curIV = CryptoServiceProvider.b64encode(CryptoServiceProvider.
                                    getIV());

                    // Escape / with _ and + with - to generate valid file names
                    curIV = b64ToFilename(curIV);

                    // Save the log file with the IV used to encrypt as its name
                    File enclog = new File(curIV + ".enclog");
                    PrintWriter pw = new PrintWriter(enclog);
                    pw.write(encrypted);
                    pw.close();

                    // Set permissions on log file
                    Set<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
                    perms.add(PosixFilePermission.OWNER_READ);
                    perms.add(PosixFilePermission.OWNER_WRITE);
                    Files.setPosixFilePermissions(Paths.get(curIV + ".enclog"), perms);

                    // delete current log file
                    if (file.delete()) {
                        System.out.println("Deleted log file:" + logLocation);
                    } else {
                        System.err.println("Error deleting log file:" + logLocation);
                    }
                } catch (FileNotFoundException fne) {
                    System.err.println("FileNotFoundException raised" + fne.getMessage());
                } catch (UnsupportedEncodingException use) {
                    System.err.println("UnsupportedEncodingException raised" + use.getMessage());
                } catch (IOException e) {
                    System.err.println("IOException raised" + e.getMessage());
                }
            }
        });

        try {
            if (startupRoutine()) {
                ServerController server = new CentralServerController(logLocation,
                    passwordsLocation);
                new ClientController(server);
            }
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            return;
        }
    }

}

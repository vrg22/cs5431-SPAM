import java.util.logging.*;

// Get instance with SimpleLogger.getLogger(...)
public class SimpleLogger extends Logger {
    private static final String LOG_FILE_LOCATION = "log.log";

    protected SimpleLogger(String name, String resourceBundleName) {
        super(name, resourceBundleName);

        FileHandler fh = new FileHandler(LOG_FILE_LOCATION, true);
        this.addHandler(fh);

        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
    }
}

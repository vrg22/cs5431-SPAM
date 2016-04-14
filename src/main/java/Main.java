import java.util.*;
import java.io.IOException;
import java.text.SimpleDateFormat;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class Main {

    public static void main(String[] args) {
        String logFileName = new SimpleDateFormat("yyyyMMddhhmm'.log'").
            format(new Date());
        String logLocation = (args != null && args.length > 0)
            ? args[0]
            : logFileName;
        String passwordsLocation = (args != null && args.length > 1)
            ? args[1]
            : "users";

        try {
            ServerController server = new CentralServerController(logLocation,
                passwordsLocation);
            new ClientController(server);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            return;
        }
    }

}

import java.util.*;
import java.io.IOException;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class Main {

    public static void main(String[] args) {
        String logLocation = (args != null && args.length > 0) ? args[0] : "log.log";
        try {
            ServerController server = new CentralServerController(logLocation);
            new ClientController(server);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            return;
        }
    }

}

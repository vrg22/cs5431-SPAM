import java.util.*;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class Main {

    public static void main(String[] args) {
        // TODO: initialize server to instance of a class which implements ServerController interface
        ServerController server = null;
        new ClientController(server);
    }

}

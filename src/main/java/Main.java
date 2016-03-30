import java.util.*;

import static spark.Spark.*;
import spark.template.handlebars.HandlebarsTemplateEngine;
import spark.ModelAndView;

public class Main {

    public static void main(String[] args) {
        new ClientController(new StoreAndRetrieveUnit());
    }

}

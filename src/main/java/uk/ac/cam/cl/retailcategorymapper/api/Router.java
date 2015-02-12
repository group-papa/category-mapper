package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Spark;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.BadInputException;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.ExceptionJsonHandler;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.config.ApiConfig;

import javax.swing.text.html.Option;

/**
 * Router class which configures Spark with our settings and routes.
 */
public class Router {
    public static void run() {
        Spark.port(ApiConfig.PORT);

        Spark.exception(BadInputException.class, new ExceptionJsonHandler());
        Spark.exception(NotFoundException.class, new ExceptionJsonHandler());

        Spark.before(new OptionsFilter());

        for (RouteBinding b : ApiConfig.BINDINGS) {
            switch (b.getMethod()) {
                case GET:
                    Spark.get(b.getPath(), b.getRoute());
                    break;
                case POST:
                    Spark.post(b.getPath(), b.getRoute());
                    break;
                case PUT:
                    Spark.put(b.getPath(), b.getRoute());
                    break;
                case DELETE:
                    Spark.delete(b.getPath(), b.getRoute());
                    break;
                case HEAD:
                    Spark.head(b.getPath(), b.getRoute());
                    break;
                case TRACE:
                    Spark.trace(b.getPath(), b.getRoute());
                    break;
                case CONNECT:
                    Spark.connect(b.getPath(), b.getRoute());
                    break;
                case OPTIONS:
                    Spark.options(b.getPath(), b.getRoute());
                    break;
            }
        }
    }
}

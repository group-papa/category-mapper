package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Filter which replies to OPTIONS requests.
 */
public class OptionsFilter implements Filter {
    @Override
    public void handle(Request request, Response response) throws Exception {
        if (request.requestMethod() == Method.OPTIONS.toString()) {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Headers",
                    request.headers("Access-Control-Request-Headers"));
            Spark.halt(200);
        }
    }
}

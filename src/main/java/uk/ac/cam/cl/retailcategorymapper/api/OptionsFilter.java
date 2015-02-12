package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * Options filter.
 */
public class OptionsFilter implements Filter {
    @Override
    public void handle(Request request, Response response) throws Exception {
        if (request.requestMethod() == "OPTIONS") {
            response.header("Access-Control-Allow-Origin", "*");
            Spark.halt(200);
        }
    }
}

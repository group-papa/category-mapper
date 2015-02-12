package uk.ac.cam.cl.retailcategorymapper.api.routes;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Abstract route which transforms the output to JSON and sets various headers.
 */
public abstract class BaseApiRoute implements Route {
    private Gson gson = new Gson();

    @Override
    public final Object handle(Request request, Response response) throws
            Exception {
        Object output = handleRequest(request, response);
        if (output == null) {
            output = new Object();
        }

        response.type("application/json");
        response.header("Access-Control-Allow-Origin", "*");

        return gson.toJson(output);
    }

    public abstract Object handleRequest(Request request, Response response)
            throws Exception;
}

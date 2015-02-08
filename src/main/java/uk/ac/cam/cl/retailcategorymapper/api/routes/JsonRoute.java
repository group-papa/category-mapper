package uk.ac.cam.cl.retailcategorymapper.api.routes;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

/**
 * Abstract route which transforms the output to JSON and sets the correct
 * response type.
 */
public abstract class JsonRoute implements Route {
    private Gson gson = new Gson();

    @Override
    public final Object handle(Request request, Response response) throws
            Exception {
        Object output = handleRequest(request, response);
        response.type("application/json");
        return gson.toJson(output);
    }

    public abstract Object handleRequest(Request request, Response response)
            throws Exception;
}

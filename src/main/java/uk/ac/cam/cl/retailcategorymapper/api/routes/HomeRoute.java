package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import spark.Route;

public class HomeRoute implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
        return "Retail Category Mapper API\n";
    }
}

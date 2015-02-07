package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

public class Api {
    public static void main(String[] args) {
        Spark.get("/", new Route() {
            @Override
            public Object handle(Request request, Response response) throws Exception {
                return "test";
            }
        });
    }
}

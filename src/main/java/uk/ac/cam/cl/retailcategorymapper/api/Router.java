package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Spark;

public class Router {
    public static void main(String[] args) {
        Spark.get("/", (request, response) -> "Retail Category Mapper API");
    }
}

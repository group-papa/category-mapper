package uk.ac.cam.cl.retailcategorymapper.api;

import spark.Route;

/**
 * Bind a path and method to a Spark route.
 */
public class RouteBinding {
    private String path;
    private Method method;
    private Route route;

    public RouteBinding(String path, Method method, Route route) {
        this.path = path;
        this.method = method;
        this.route = route;
    }

    public String getPath() {
        return path;
    }

    public Method getMethod() {
        return method;
    }

    public Route getRoute() {
        return route;
    }
}

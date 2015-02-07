package uk.ac.cam.cl.retailcategorymapper.config;

import uk.ac.cam.cl.retailcategorymapper.api.Method;
import uk.ac.cam.cl.retailcategorymapper.api.RouteBinding;
import uk.ac.cam.cl.retailcategorymapper.api.routes.HomeRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration options for the API.
 */
public final class ApiConfig {
    /**
     * Port to run the API on.
     */
    public static final int PORT = 8000;

    /**
     * Routes.
     */
    public static final List<RouteBinding> bindings = new ArrayList<>(
            Arrays.asList(
                    new RouteBinding("/", Method.GET, new HomeRoute())
            ));

    /**
     * Prevent instantiation.
     */
    private ApiConfig() {}
}

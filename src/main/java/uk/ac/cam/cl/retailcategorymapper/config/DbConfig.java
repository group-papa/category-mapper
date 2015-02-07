package uk.ac.cam.cl.retailcategorymapper.config;

import uk.ac.cam.cl.retailcategorymapper.api.Method;
import uk.ac.cam.cl.retailcategorymapper.api.RouteBinding;
import uk.ac.cam.cl.retailcategorymapper.api.routes.HomeRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Configuration options for the database.
 */
public final class DbConfig {
    /**
     * Redis server address.
     */
    public static final String ADDRESS = "127.0.0.1:6379";

    /**
     * Prevent instantiation.
     */
    private DbConfig() {}
}

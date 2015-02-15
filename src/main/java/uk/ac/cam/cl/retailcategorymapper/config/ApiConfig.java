package uk.ac.cam.cl.retailcategorymapper.config;

import uk.ac.cam.cl.retailcategorymapper.api.Method;
import uk.ac.cam.cl.retailcategorymapper.api.RouteBinding;
import uk.ac.cam.cl.retailcategorymapper.api.routes.AddTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.AddUploadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.DeleteTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetUploadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.HomeRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.ListTaxonomiesRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.ListUploadsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Configuration options for the API.
 */
public final class ApiConfig {
    /**
     * Port to run the API on.
     */
    public static final int PORT;

    /**
     * Static initialiser.
     */
    static {
        Map<String, String> config = PropertiesLoader.getProperties("api");
        PORT = Integer.parseInt(config.getOrDefault("port", "8000"));
    }

    /**
     * Routes.
     */
    public static final List<RouteBinding> BINDINGS = new ArrayList<>(
            Arrays.asList(
                    new RouteBinding("/", Method.GET,
                            new HomeRoute()),

                    new RouteBinding("/taxonomies", Method.GET,
                            new ListTaxonomiesRoute()),
                    new RouteBinding("/taxonomies", Method.POST,
                            new AddTaxonomyRoute()),
                    new RouteBinding("/taxonomies/:taxonomy[id]", Method.GET,
                            new GetTaxonomyRoute()),
                    new RouteBinding("/taxonomies/:taxonomy[id]", Method.DELETE,
                            new DeleteTaxonomyRoute()),

                    new RouteBinding("/uploads", Method.GET,
                            new ListUploadsRoute()),
                    new RouteBinding("/uploads", Method.POST,
                            new AddUploadRoute()),
                    new RouteBinding("/uploads/:upload[id]", Method.GET,
                            new GetUploadRoute())
            ));

    /**
     * Prevent instantiation.
     */
    private ApiConfig() {}
}

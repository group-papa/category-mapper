package uk.ac.cam.cl.retailcategorymapper.config;

import uk.ac.cam.cl.retailcategorymapper.api.Method;
import uk.ac.cam.cl.retailcategorymapper.api.RouteBinding;
import uk.ac.cam.cl.retailcategorymapper.api.routes.AddTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.AddUploadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.ClassifyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.DeleteTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.DeleteUploadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetDownloadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetTaxonomyRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetUploadRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.GetUploadedProductRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.HomeRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.ListTaxonomiesRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.ListUploadsRoute;
import uk.ac.cam.cl.retailcategorymapper.api.routes.TrainRoute;

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
                    new RouteBinding("/",
                            Method.GET,
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
                            new GetUploadRoute()),
                    new RouteBinding("/uploads/:upload[id]", Method.DELETE,
                            new DeleteUploadRoute()),

                    new RouteBinding("/products/:upload[id]/:product[id]",
                            Method.GET, new GetUploadedProductRoute()),

                    new RouteBinding("/classify", Method.POST,
                            new ClassifyRoute()),
                    new RouteBinding("/train", Method.POST,
                            new TrainRoute()),

                    new RouteBinding("/downloads/:download[id]", Method.GET,
                            new GetDownloadRoute())
            ));

    /**
     * Prevent instantiation.
     */
    private ApiConfig() {}
}

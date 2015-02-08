package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;

/**
 * List taxonomies stored in the database.
 */
public class ListTaxonomiesRoute extends JsonRoute {
    @Override
    public Object handleRequest(Request request, Response response) throws Exception {
        return TaxonomyDb.getTaxonomies();
    }
}

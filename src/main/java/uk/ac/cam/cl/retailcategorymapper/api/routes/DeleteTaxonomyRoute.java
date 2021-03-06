package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;

/**
 * Delete a taxonomy.
 */
public class DeleteTaxonomyRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyId = request.params(":taxonomy[id]");

        boolean result = TaxonomyDb.deleteTaxonomy(taxonomyId);
        if (!result) {
            throw new NotFoundException("Unknown taxonomy ID.");
        }

        return null;
    }
}

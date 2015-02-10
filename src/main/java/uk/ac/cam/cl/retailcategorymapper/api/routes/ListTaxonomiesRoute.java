package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;

/**
 * List taxonomies stored in the database.
 */
public class ListTaxonomiesRoute extends JsonRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        return new ListTaxonomiesReply(TaxonomyDb.getTaxonomies());
    }

    class ListTaxonomiesReply {
        List<Taxonomy> taxonomies;

        public ListTaxonomiesReply(List<Taxonomy> taxonomies) {
            this.taxonomies = taxonomies;
        }
    }
}

package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.ArrayList;

/**
 * Add a new taxonomy to the database.
 */
public class AddTaxonomyRoute extends JsonRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyName = request.queryParams("taxonomyName");
        if (taxonomyName == null) {
            throw new IllegalArgumentException("taxonomyName must be " +
                    "provided.");
        }

        Taxonomy taxonomy = new TaxonomyBuilder().setId(Uuid.generateUUID())
                .setName(taxonomyName).createTaxonomy();
        // TODO: Store categories as well.
        TaxonomyDb.setTaxonomy(taxonomy, new ArrayList<>());

        return new AddTaxonomyReply(taxonomy.getId());
    }

    class AddTaxonomyReply {
        String taxonomyId;

        public AddTaxonomyReply(String taxonomyId) {
            this.taxonomyId = taxonomyId;
        }
    }
}

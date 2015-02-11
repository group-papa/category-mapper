package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.BadInputException;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.CategoryFileUnmarshaller;
import uk.ac.cam.cl.retailcategorymapper.utils.DateTime;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.List;

/**
 * Add a new taxonomy to the database.
 */
public class AddTaxonomyRoute extends JsonRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyName = request.queryParams("taxonomyName");
        if (taxonomyName == null) {
            throw new BadInputException("taxonomyName must be provided.");
        }

        Taxonomy taxonomy = new TaxonomyBuilder()
                .setId(Uuid.generateUUID())
                .setName(taxonomyName)
                .setDateCreated(DateTime.getCurrentTimeIso8601())
                .createTaxonomy();

        // TODO: Get categories from request.
        String categoryData = "";
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal(categoryData);

        TaxonomyDb.setTaxonomy(taxonomy, categories);

        return GetTaxonomyRoute.generateGetTaxonomyReply(taxonomy);
    }
}

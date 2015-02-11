package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Get metadata for a specific taxonomy.
 */
public class GetTaxonomyRoute extends JsonRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyId = request.params(":id");

        Taxonomy taxonomy = TaxonomyDb.getTaxonomy(taxonomyId);
        if (taxonomy == null) {
            throw new NotFoundException("Unknown taxonomyId.");
        }

        List<Category> categories = TaxonomyDb
                .getCategoriesForTaxonomy(taxonomyId);
        List<String> categoryIds = categories.stream()
                .map(Category::getId).collect(Collectors.toList());

        return new GetTaxonomyReply(new TaxonomyEntry(taxonomy.getId(),
                taxonomy.getName(), taxonomy.getDateCreated(), categoryIds),
                categories);
    }

    class GetTaxonomyReply {
        TaxonomyEntry taxonomy;
        List<Category> categories;

        public GetTaxonomyReply(TaxonomyEntry taxonomy,
                                List<Category> categories) {
            this.taxonomy = taxonomy;
            this.categories = categories;
        }
    }

    class TaxonomyEntry {
        private String id;
        private String name;
        private String dateCreated;
        private List<String> categories;

        public TaxonomyEntry(String id, String name, String dateCreated,
                             List<String> categories) {
            this.id = id;
            this.name = name;
            this.dateCreated = dateCreated;
            this.categories = categories;
        }
    }
}

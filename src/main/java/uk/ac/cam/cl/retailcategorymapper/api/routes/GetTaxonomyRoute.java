package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 * Get metadata for a specific taxonomy.
 */
public class GetTaxonomyRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyId = request.params(":taxonomy[id]");

        Taxonomy taxonomy = TaxonomyDb.getTaxonomy(taxonomyId);
        if (taxonomy == null) {
            throw new NotFoundException("Unknown taxonomy ID.");
        }

        return generateGetTaxonomyReply(taxonomy);
    }

    static GetTaxonomyReply generateGetTaxonomyReply(Taxonomy taxonomy) {
        List<Category> categories = TaxonomyDb
                .getCategoriesForTaxonomy(taxonomy.getId());
        List<String> categoryIds = new ArrayList<>();
        for (Category category : categories) {
            categoryIds.add(category.getId());
        }

        return new GetTaxonomyReply(new TaxonomyEntry(taxonomy.getId(),
                taxonomy.getName(), taxonomy.getDateCreated(), categoryIds),
                categories);
    }

    static class GetTaxonomyReply {
        TaxonomyEntry taxonomy;
        List<Category> categories;

        public GetTaxonomyReply(TaxonomyEntry taxonomy,
                                List<Category> categories) {
            this.taxonomy = taxonomy;
            this.categories = categories;
        }
    }

    static class TaxonomyEntry {
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

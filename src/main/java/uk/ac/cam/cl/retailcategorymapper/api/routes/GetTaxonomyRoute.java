package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Set;
import java.util.stream.Collectors;

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
        Set<Category> categories = TaxonomyDb
                .getCategoriesForTaxonomy(taxonomy);
        Set<String> categoryIds = categories.stream().map(Category::getId)
                .collect(Collectors.toSet());

        return new GetTaxonomyReply(new TaxonomyEntry(taxonomy.getId(),
                taxonomy.getName(), taxonomy.getDateCreated(), categoryIds),
                categories);
    }

    static class GetTaxonomyReply {
        TaxonomyEntry taxonomy;
        Set<Category> categories;

        public GetTaxonomyReply(TaxonomyEntry taxonomy,
                                Set<Category> categories) {
            this.taxonomy = taxonomy;
            this.categories = categories;
        }
    }

    static class TaxonomyEntry {
        private String id;
        private String name;
        private String dateCreated;
        private Set<String> categories;

        public TaxonomyEntry(String id, String name, String dateCreated,
                             Set<String> categories) {
            this.id = id;
            this.name = name;
            this.dateCreated = dateCreated;
            this.categories = categories;
        }
    }
}

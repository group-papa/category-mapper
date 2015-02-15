package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.List;

/**
 * List taxonomies stored in the database.
 */
public class ListTaxonomiesRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        List<Taxonomy> taxonomies = TaxonomyDb.getTaxonomies();

        List<TaxonomyEntry> results = new ArrayList<>();
        for (Taxonomy taxonomy : taxonomies) {
            List<Category> categories = TaxonomyDb
                    .getCategoriesForTaxonomy(taxonomy);

            List<String> categoryIds = new ArrayList<>();
            for (Category category : categories) {
                categoryIds.add(category.getId());
            }

            results.add(new TaxonomyEntry(taxonomy.getId(),
                    taxonomy.getName(), taxonomy.getDateCreated(),
                    categoryIds));
        }

        return new ListTaxonomiesReply(results);
    }

    static class ListTaxonomiesReply {
        List<TaxonomyEntry> taxonomies;

        public ListTaxonomiesReply(List<TaxonomyEntry> taxonomies) {
            this.taxonomies = taxonomies;
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

package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
            Set<Category> categories = TaxonomyDb
                    .getCategoriesForTaxonomy(taxonomy);

            Set<String> categoryIds = categories.stream().map(Category::getId)
                    .collect(Collectors.toSet());

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

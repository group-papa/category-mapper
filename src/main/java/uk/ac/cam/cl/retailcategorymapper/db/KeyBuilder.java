package uk.ac.cam.cl.retailcategorymapper.db;

import org.apache.commons.lang3.StringUtils;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

/**
 * Helper class for building Redis keys.
 */
class KeyBuilder {
    private static final String SEPARATOR = ":";
    private static final String TAXONOMY = "taxonomy";
    private static final String INSTANCE = "instance";
    private static final String CATEGORIES = "categories";
    private static final String MANUAL = "manual";
    private static final String NAIVE = "naive";
    private static final String FEATURE = "naive";
    private static final String UPLOAD = "upload";

    private static String build(String... parts) {
        return StringUtils.join(parts, SEPARATOR);
    }

    public static String taxonomyInstance(Taxonomy taxonomy) {
        return build(TAXONOMY, taxonomy.getId(), INSTANCE);
    }

    public static String allTaxonomyInstances() {
        return build(TAXONOMY, "*", INSTANCE);
    }

    public static String categoriesForTaxonomy(Taxonomy taxonomy) {
        return build(TAXONOMY, taxonomy.getId(), CATEGORIES);
    }

    public static String manualMapping(Taxonomy taxonomy, Product product) {
        return build(TAXONOMY, taxonomy.getId(), MANUAL, product.getId());
    }

    public static String naiveCategory(Taxonomy taxonomy, Category category) {
        return build(TAXONOMY, taxonomy.getId(), NAIVE, category.toString());
    }

    public static String naiveFeature(Taxonomy taxonomy, Category category,
                                      String feature) {
        return build(TAXONOMY, taxonomy.getId(), NAIVE, category.toString(),
                FEATURE, feature);
    }

    public static String upload(String uploadId) {
        return build(UPLOAD, uploadId);
    }
}

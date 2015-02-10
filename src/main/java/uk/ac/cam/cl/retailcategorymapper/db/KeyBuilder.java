package uk.ac.cam.cl.retailcategorymapper.db;

import org.apache.commons.lang3.StringUtils;

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

    public static String taxonomyInstance(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, INSTANCE);
    }

    public static String allTaxonomyInstances() {
        return build(TAXONOMY, "*", INSTANCE);
    }

    public static String categoriesForTaxonomy(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, CATEGORIES);
    }

    public static String manualMapping(String taxonomyId, String productId) {
        return build(TAXONOMY, taxonomyId, MANUAL, productId);
    }

    public static String naiveCategory(String taxonomyId, String category) {
        return build(TAXONOMY, taxonomyId, NAIVE, category);
    }

    public static String naiveFeature(String taxonomyId, String category,
                                      String feature) {
        return build(TAXONOMY, taxonomyId, NAIVE, category, FEATURE, feature);
    }

    public static String upload(String uploadId) {
        return build(UPLOAD, uploadId);
    }
}

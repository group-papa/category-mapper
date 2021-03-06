package uk.ac.cam.cl.retailcategorymapper.db;

import org.apache.commons.lang3.StringUtils;

/**
 * Helper class for building Redis keys.
 */
class KeyBuilder {
    private static final String SEPARATOR = ":";
    private static final String INSTANCE = "instance";

    private static final String TAXONOMY = "taxonomy";
    private static final String CATEGORIES = "categories";

    private static final String MANUAL = "manual";

    private static final String NAIVE = "naive";
    private static final String PRODUCTS = "products";
    private static final String CATPRODCOUNT = "catprodcount";
    private static final String CATFEATCOUNT = "catfeatcount";
    private static final String CATEGORY = "category";
    private static final String FEATOBSCOUNT = "featobscount";
    private static final String FEATURES = "features";

    private static final String UPLOAD = "upload";
    private static final String MAPPINGS = "mappings";

    private static final String DOWNLOAD = "download";

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

    public static String taxonomyFamily(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, "*");
    }

    public static String manualMapping(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, MANUAL);
    }

    public static String naiveProductsCount(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, NAIVE, PRODUCTS);
    }

    public static String naiveCategoryProductCount(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, NAIVE, CATPRODCOUNT);
    }

    public static String naiveCategoryFeatureCount(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, NAIVE, CATFEATCOUNT);
    }

    public static String naiveFeatureObservationCount(String taxonomyId,
                                                      String categoryId) {
        return build(TAXONOMY, taxonomyId, NAIVE, CATEGORY, categoryId,
                FEATOBSCOUNT);
    }

    public static String naiveFeatureSet(String taxonomyId) {
        return build(TAXONOMY, taxonomyId, NAIVE, FEATURES);
    }

    public static String uploadInstance(String uploadId) {
        return build(UPLOAD, uploadId, INSTANCE);
    }

    public static String allUploadInstances() {
        return build(UPLOAD, "*", INSTANCE);
    }

    public static String uploadProducts(String uploadId) {
        return build(UPLOAD, uploadId, PRODUCTS);
    }

    public static String uploadMappings(String uploadId) {
        return build(UPLOAD, uploadId, MAPPINGS);
    }

    public static String uploadFamily(String uploadId) {
        return build(UPLOAD, uploadId, "*");
    }

    public static String download(String downloadId) {
        return build(DOWNLOAD, downloadId);
    }
}

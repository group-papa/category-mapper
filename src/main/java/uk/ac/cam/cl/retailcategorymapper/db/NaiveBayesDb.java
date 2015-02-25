package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesStorage;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Map;
import java.util.Set;

/**
 * Methods for persisting values required by the Naive Bayes Classifier.
 */
public class NaiveBayesDb implements NaiveBayesStorage {
    /**
     * Get the count of total products seen in a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The product count.
     */

    private NaiveBayesDb() {

    }

    private static NaiveBayesDb instance = null;

    public static NaiveBayesDb getInstance() {
        if (instance == null) instance = new NaiveBayesDb();
        return instance;
    }

    public int getProductCount(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveProductsCount(taxonomy.getId());
        RBucket<Integer> productCountRBucket = redisson.getBucket(key);

        if (!productCountRBucket.exists()) {
            return 0;
        }

        return productCountRBucket.get();
    }

    /**
     * Set the count of total products seen in a taxonomy.
     * @param taxonomy The taxonomy.
     * @param count The new count.
     */
    public void setProductCount(Taxonomy taxonomy, int count) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveProductsCount(taxonomy.getId());
        RBucket<Integer> productCountRBucket = redisson.getBucket(key);

        productCountRBucket.set(count);
    }

    /**
     * Get a category to product count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The category product count map.
     */
    public Map<Category, Integer> getCategoryProductMap(
            Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveCategoryProductCount(taxonomy.getId());

        return redisson.getMap(key);
    }

    /**
     * Get a category to feature count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The category feature count map.
     */
    public Map<Category, Integer> getCategoryFeatureMap(
            Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveCategoryFeatureCount(taxonomy.getId());

        return redisson.getMap(key);
    }

    @Override
    public Map<Category, Taxonomy> getCategorySubTaxonomyMap(Taxonomy taxonomy) {
        // TODO FIXME
        return null;
    }

    /**
     * Get a category's feature to observation count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @param category The category.
     * @return The category feature observation count map.
     */
    public Map<Feature, Integer> getCategoryFeatureObservationMap(
            Taxonomy taxonomy, Category category) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveFeatureObservationCount(taxonomy.getId(),
                category.getId());

        return redisson.getMap(key);
    }

    /**
     * Get a feature set for a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The feature set.
     */
    public Set<Feature> getFeatureSet(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveFeatureSet(taxonomy.getId());

        return redisson.getSet(key);
    }
}

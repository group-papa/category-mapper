package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Map;
import java.util.Set;

/**
 * Methods for persisting values required by the Naive Bayes Classifier.
 */
public class NaiveBayesDb {
    /**
     * Get the count of total products seen in a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The product count.
     */
    public static int getProductCount(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveProductsCount(taxonomy.getId());
        RBucket<Integer> productCountRBucket = redisson.getBucket(key);

        if (!productCountRBucket.exists()) {
            return 0;
        }

        return productCountRBucket.get();
    }

    /**
     * Increment the count of total products seen in a taxonomy by 1.
     * @param taxonomy The taxonomy.
     * @return The new product count.
     */
    public static int incrementProductCount(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveProductsCount(taxonomy.getId());
        RBucket<Integer> productCountRBucket = redisson.getBucket(key);

        int count;
        if (productCountRBucket.exists()) {
            count = productCountRBucket.get() + 1;
        } else {
            count = 1;
        }
        productCountRBucket.set(count);

        return count;
    }

    /**
     * Get a category product count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The category product count map.
     */
    public static Map<Category, Integer> getCategoryProductCount(
            Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveCategoryProductCount(taxonomy.getId());

        return redisson.getMap(key);
    }

    /**
     * Get a category feature count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The category feature count map.
     */
    public static Map<Category, Integer> getCategoryFeatureMap(
            Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveCategoryFeatureCount(taxonomy.getId());

        return redisson.getMap(key);
    }

    /**
     * Get a category feature observation count map for a taxonomy.
     * @param taxonomy The taxonomy.
     * @param category The category.
     * @return The category feature observation count map.
     */
    public static Map<Category, Integer> getCategoryFeatureObservationMap(
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
    public static Set<Feature> getFeatureSet(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.naiveFeatureSet(taxonomy.getId());

        return redisson.getSet(key);
    }
}

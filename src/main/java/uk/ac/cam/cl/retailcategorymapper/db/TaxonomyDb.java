package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RSet;
import redis.clients.jedis.Jedis;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Methods for persisting taxonomies to the underlying database.
 */
public class TaxonomyDb {
    /**
     * List all taxonomies stored in the database.
     * @return List of taxonomies.
     */
    public static List<Taxonomy> getTaxonomies() {
        Jedis jedis = JedisWrapper.getInstance();
        Redisson redisson = RedissonWrapper.getInstance();

        Set<String> keys = jedis.keys(KeyBuilder.allTaxonomyInstances());

        List<Taxonomy> result = new ArrayList<>();
        for (String key : keys) {
            RBucket<Taxonomy> taxonomyRBucket = redisson.getBucket(key);
            Taxonomy taxonomy = taxonomyRBucket.get();
            if (taxonomy != null) {
                result.add(taxonomy);
            }
        }

        return result;
    }

    /**
     * Get a taxonomy.
     * @param taxonomyId The taxonomy ID to get.
     * @return Fetched taxonomy.
     */
    public static Taxonomy getTaxonomy(String taxonomyId)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.taxonomyInstance(taxonomyId);
        RBucket<Taxonomy> taxonomyRBucket = redisson.getBucket(instanceKey);
        return taxonomyRBucket.get();
    }

    /**
     * Get the categories in a taxonomy.
     * @param taxonomy The taxonomy.
     * @return The taxonomy's categories.
     */
    public static Set<Category> getCategoriesForTaxonomy(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String categoriesKey = KeyBuilder.categoriesForTaxonomy(
                taxonomy.getId());
        RSet<Category> categoryRSet = redisson.getSet(categoriesKey);
        return new HashSet<>(categoryRSet);
    }

    /**
     * Store a taxonomy; if one already exists, it will be overwritten.
     * @param taxonomy The taxonomy to s.
     * @param categories The taxonomy's categories.
     */
    public static void setTaxonomy(Taxonomy taxonomy,
                                   Set<Category> categories)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.taxonomyInstance(taxonomy.getId());
        RBucket<Taxonomy> taxonomyRBucket = redisson.getBucket(instanceKey);
        taxonomyRBucket.set(taxonomy);

        String categoriesKey = KeyBuilder.categoriesForTaxonomy(
                taxonomy.getId());
        RSet<Category> categoryRSet = redisson.getSet(categoriesKey);
        categoryRSet.clear();
        categoryRSet.addAll(categories);
    }

    /**
     * Delete a taxonomy, together with its categories, probabilities and
     * manual mappings.
     * @param taxonomyId The ID for the taxonomy to delete.
     * @return Whether the taxonomy existed.
     */
    public static boolean deleteTaxonomy(String taxonomyId) {
        Jedis jedis = JedisWrapper.getInstance();

        Set<String> keys = jedis.keys(KeyBuilder.taxonomyFamily(taxonomyId));

        if (keys.size() == 0) {
            return false;
        }

        jedis.del(keys.toArray(new String[keys.size()]));

        return true;
    }
}

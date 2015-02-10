package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RList;
import redis.clients.jedis.Jedis;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
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
            RBucket<Taxonomy> bucket = redisson.getBucket(key);
            Taxonomy taxonomy = bucket.get();
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
     * Store a taxonomy; if one already exists, it will be overwritten.
     * @param taxonomy The taxonomy to add.
     * @param categories The taxonomy's categories.
     */
    public static void setTaxonomy(Taxonomy taxonomy,
                                   List<Category> categories)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.taxonomyInstance(taxonomy.getId());
        RBucket<Taxonomy> taxonomyRBucket = redisson.getBucket(instanceKey);
        taxonomyRBucket.set(taxonomy);

        String categoriesKey = KeyBuilder.categoriesForTaxonomy(
                taxonomy.getId());
        RList<Category> rList = redisson.getList(categoriesKey);
        rList.clear();
        rList.addAll(categories);
    }
}

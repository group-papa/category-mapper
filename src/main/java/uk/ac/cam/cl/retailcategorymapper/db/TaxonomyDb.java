package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import redis.clients.jedis.Jedis;
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
}

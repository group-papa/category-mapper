package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Map;

/**
 * Methods for persisting values required by the Manual Mappings module.
 */
public class ManualMappingDb {
    /**
     * Get the manual mappings for a given taxonomy.
     * @param taxonomy The taxonomy.
     * @return The manual mappings.
     */
    public static Map<String, Category> getManualMappings(Taxonomy taxonomy) {
        Redisson redisson = RedissonWrapper.getInstance();

        String key = KeyBuilder.manualMapping(taxonomy.getId());

        return redisson.getMap(key);
    }
}

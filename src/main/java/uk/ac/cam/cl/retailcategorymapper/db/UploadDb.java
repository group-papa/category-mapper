package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RMap;
import redis.clients.jedis.Jedis;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Methods for persisting uploads to the underlying database.
 */
public class UploadDb {
    /**
     * List all uploads stored in the database.
     * @return List of uploads.
     */
    public static List<Upload> getUploads() {
        Jedis jedis = JedisWrapper.getInstance();
        Redisson redisson = RedissonWrapper.getInstance();

        Set<String> keys = jedis.keys(KeyBuilder.allUploadInstances());

        JedisWrapper.returnInstance(jedis);

        List<Upload> result = new ArrayList<>();
        for (String key : keys) {
            RBucket<Upload> uploadRBucket = redisson.getBucket(key);
            Upload upload = uploadRBucket.get();
            if (upload != null) {
                result.add(upload);
            }
        }

        return result;
    }

    /**
     * Get an upload.
     * @param uploadId The upload ID to get.
     * @return Fetched upload.
     */
    public static Upload getUpload(String uploadId)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.uploadInstance(uploadId);
        RBucket<Upload> uploadRBucket = redisson.getBucket(instanceKey);
        return uploadRBucket.get();
    }

    /**
     * Get an upload's products.
     * @param upload The upload.
     * @return Fetched products.
     */
    public static Map<String, Product> getUploadProducts(Upload upload)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String productsKey = KeyBuilder.uploadProducts(upload.getId());
        RMap<String, Product> productsRMap = redisson.getMap(productsKey);
        return new HashMap<>(productsRMap);
    }

    /**
     * Get an upload's mappings.
     * @param upload The upload.
     * @return Fetched mappings.
     */
    public static Map<String, Mapping> getUploadMappings(Upload upload)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String mappingsKey = KeyBuilder.uploadMappings(upload.getId());
        RMap<String, Mapping> mappingsRMap = redisson.getMap(mappingsKey);
        return new HashMap<>(mappingsRMap);
    }

    /**
     * Store an upload; if one already exists, it will be overwritten.
     * @param upload The upload to store.
     * @param products The products to store.
     * @param mappings The mappings to store.
     */
    public static void setUpload(Upload upload, Map<String, Product> products,
                                 Map<String, Mapping> mappings) {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.uploadInstance(upload.getId());
        RBucket<Upload> uploadRBucket = redisson.getBucket(instanceKey);
        uploadRBucket.set(upload);

        String productsKey = KeyBuilder.uploadProducts(upload.getId());
        RMap<String, Product> productsRMap = redisson.getMap(productsKey);
        productsRMap.clear();
        productsRMap.putAll(products);

        String mappingsKey = KeyBuilder.uploadMappings(upload.getId());
        RMap<String, Mapping> mappingsRMap = redisson.getMap(mappingsKey);
        mappingsRMap.clear();
        mappingsRMap.putAll(mappings);
    }

    /**
     * Delete an upload along with its products and mappings.
     * @param uploadId The ID for the upload to delete.
     * @return Whether the upload existed.
     */
    public static boolean deleteUpload(String uploadId) {
        Jedis jedis = JedisWrapper.getInstance();

        Set<String> keys = jedis.keys(KeyBuilder.uploadFamily(uploadId));

        if (keys.size() == 0) {
            return false;
        }

        jedis.del(keys.toArray(new String[keys.size()]));

        JedisWrapper.returnInstance(jedis);

        return true;
    }
}

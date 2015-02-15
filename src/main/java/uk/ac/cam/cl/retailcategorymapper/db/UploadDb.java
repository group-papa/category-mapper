package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import org.redisson.core.RList;
import redis.clients.jedis.Jedis;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.ArrayList;
import java.util.List;
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
     * Store an upload; if one already exists, it will be overwritten.
     * @param upload The upload to store.
     * @param products The products to store.
     * @param mappings The mappings to store.
     */
    public static void setUpload(Upload upload, List<Product> products,
                                 List<Mapping> mappings) {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.uploadInstance(upload.getId());
        RBucket<Upload> uploadRBucket = redisson.getBucket(instanceKey);
        uploadRBucket.set(upload);

        String productsKey = KeyBuilder.uploadProducts(upload);
        RList<Product> productsRList = redisson.getList(productsKey);
        productsRList.clear();
        productsRList.addAll(products);

        String mappingsKey = KeyBuilder.uploadProducts(upload);
        RList<Mapping> mappingsRList = redisson.getList(mappingsKey);
        mappingsRList.clear();
        mappingsRList.addAll(mappings);
    }
}

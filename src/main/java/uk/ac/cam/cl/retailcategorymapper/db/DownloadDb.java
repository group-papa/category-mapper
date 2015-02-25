package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Redisson;
import org.redisson.core.RBucket;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.concurrent.TimeUnit;

/**
 * Methods for persisting previous classification runs to the underlying
 * database for downloading later.
 */
public class DownloadDb {
    public static long DOWNLOAD_TTL_DAYS = 7;

    /**
     * Get a download.
     * @param downloadId The download ID to get.
     * @return Fetched download.
     */
    public static String getDownload(String downloadId)  {
        Redisson redisson = RedissonWrapper.getInstance();

        String instanceKey = KeyBuilder.download(downloadId);
        RBucket<String> downloadRBucket = redisson.getBucket(instanceKey);
        return downloadRBucket.get();
    }

    /**
     * Store a download; if one already exists, it will be overwritten.
     * @param download The download to store.
     * @return The ID to store the download under.
     */
    public static String setDownload(String download) {
        Redisson redisson = RedissonWrapper.getInstance();

        String id = Uuid.generateUUID();

        String instanceKey = KeyBuilder.download(id);
        RBucket<String> downloadRBucket = redisson.getBucket(instanceKey);
        downloadRBucket.set(download, DOWNLOAD_TTL_DAYS, TimeUnit.DAYS);

        return id;
    }
}

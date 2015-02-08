package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Config;
import org.redisson.Redisson;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

/**
 * Simple wrapper around Redisson which implements the singleton pattern.
 */
class RedissonWrapper {
    private static Redisson redisson;

    public static Redisson getInstance() {
        if (redisson == null) {
            Config config = new Config();
            String address = DbConfig.HOST + ":" + DbConfig.PORT;
            config.useSingleServer().setAddress(address);
            redisson = Redisson.create(config);
        }
        return redisson;
    }

    public static void close() {
        if (redisson != null) {
            redisson.shutdown();
            redisson = null;
        }
    }
}

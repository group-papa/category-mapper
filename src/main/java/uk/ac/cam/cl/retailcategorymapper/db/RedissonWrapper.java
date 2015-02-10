package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Config;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

/**
 * Simple wrapper around Redisson which implements the singleton pattern.
 */
class RedissonWrapper {
    private static Redisson redisson;

    /**
     * Private constructor to prevent instantiation.
     */
    private RedissonWrapper() {}

    public static Redisson getInstance() {
        if (redisson == null) {
            Config config = new Config();
            config.setCodec(new JsonJacksonCodec());
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

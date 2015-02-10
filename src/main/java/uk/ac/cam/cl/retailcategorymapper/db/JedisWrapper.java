package uk.ac.cam.cl.retailcategorymapper.db;

import redis.clients.jedis.Jedis;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

/**
 * Simple wrapper around Jedis which implements the singleton pattern.
 */
class JedisWrapper {
    private static Jedis jedis;

    /**
     * Private constructor to prevent instantitation.
     */
    private JedisWrapper() {}

    public static Jedis getInstance() {
        if (jedis == null) {
            jedis = new Jedis(DbConfig.HOST, DbConfig.PORT);
        }
        return jedis;
    }

    public static void close() {
        if (jedis != null) {
            jedis.close();
            jedis = null;
        }
    }
}

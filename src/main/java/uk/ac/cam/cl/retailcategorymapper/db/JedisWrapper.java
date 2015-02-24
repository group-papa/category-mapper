package uk.ac.cam.cl.retailcategorymapper.db;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

/**
 * Simple wrapper around Jedis which implements the singleton pattern.
 */
class JedisWrapper {
    private static JedisPool pool;

    /**
     * Private constructor to prevent instantitation.
     */
    private JedisWrapper() {}

    public static void initialise() {
        close();
        pool = new JedisPool(new JedisPoolConfig(),
                DbConfig.HOST, DbConfig.PORT);
    }

    public static Jedis getInstance() {
        if (pool == null) {
            initialise();
        }
        return pool.getResource();
    }

    public static void returnInstance(Jedis jedis) {
        if (pool != null) {
            pool.returnResource(jedis);
        }
    }

    public static void close() {
        if (pool != null) {
            pool.destroy();
            pool = null;
        }
    }
}

package uk.ac.cam.cl.retailcategorymapper.db;

/**
 * Initialise connections to the database.
 */
public class InitialiseDb {
    public static void initialise() {
        JedisWrapper.getInstance();
        RedissonWrapper.getInstance();
    }
}

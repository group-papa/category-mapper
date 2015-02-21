package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Config;
import org.redisson.MasterSlaveServersConfig;
import org.redisson.Redisson;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.connection.MasterSlaveConnectionManager;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

import java.lang.reflect.Field;

/**
 * Simple wrapper around Redisson which implements the singleton pattern.
 */
class RedissonWrapper {
    private static Redisson redisson;

    /**
     * Private constructor to prevent instantiation.
     */
    private RedissonWrapper() {}

    public static void initialise() {
        close();
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        String address = DbConfig.HOST + ":" + DbConfig.PORT;
        config.useSingleServer().setAddress(address);
        redisson = Redisson.create(config);
        timeoutPatch(redisson);
    }

    public static Redisson getInstance() {
        if (redisson == null) {
            initialise();
        }
        return redisson;
    }

    public static void close() {
        if (redisson != null) {
            redisson.shutdown();
            redisson = null;
        }
    }

    /**
     * This is a temporary patch to workaround the (temporary) memory leak we
     * discovered in Redisson.
     *
     * When a promise has been fulfilled, the timeout task should be cancelled.
     * Currently this is not the case and means that the (potentially very
     * large) amounts of data included in the promise will remain on the heap
     * until the timeout fires.
     *
     * Daniel and Priyesh have reported the issue to the Redisson team
     * (https://github.com/mrniko/redisson/issues/123) and if this gets fixed
     * we'll remove this patch and ensure the memory leak has not returned.
     *
     * The patch works by setting the timeout to something much lower than
     * the default of 60 seconds. (The real solution is to have the timeout
     * task removed when the promise is fulfilled.)
     *
     * We need to patch Redisson rather than the Config we provide to Redisson,
     * as Redisson ignores the timeout set in a SingleServerConfig provided
     * upon construction.
     *
     * @param redisson The Redisson instance to patch.
     */
    private static void timeoutPatch(Redisson redisson) {
        Field cmField;
        try {
            cmField = Redisson.class.getDeclaredField("connectionManager");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        cmField.setAccessible(true);
        MasterSlaveConnectionManager cm;
        try {
            cm = (MasterSlaveConnectionManager) cmField.get(redisson);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        Field configField;
        try {
            configField = MasterSlaveConnectionManager.class.getDeclaredField
                    ("config");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            return;
        }

        configField.setAccessible(true);
        MasterSlaveServersConfig config;
        try {
            config = (MasterSlaveServersConfig) configField.get(cm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return;
        }

        config.setTimeout(3000);
    }
}

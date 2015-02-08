package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Config;
import org.redisson.Redisson;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

public class Db {
    private static Redisson redisson;

    public static Redisson getInstance() {
        if (redisson == null) {
            Config config = new Config();
            config.useSingleServer().setAddress(DbConfig.ADDRESS);
            redisson = Redisson.create(config);
        }
        return redisson;
    }

    public static void close() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }
}

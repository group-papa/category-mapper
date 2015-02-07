package uk.ac.cam.cl.retailcategorymapper.db;

import org.redisson.Config;
import org.redisson.Redisson;
import uk.ac.cam.cl.retailcategorymapper.config.DbConfig;

public class DbController {
    public void init() {
        Config config = new Config();
        config.useSingleServer().setAddress(DbConfig.ADDRESS);
        Redisson redisson = Redisson.create(config);
    }
}

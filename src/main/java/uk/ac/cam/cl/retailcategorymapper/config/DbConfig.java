package uk.ac.cam.cl.retailcategorymapper.config;

import java.util.Map;

/**
 * Configuration options for the database.
 */
public final class DbConfig {
    /**
     * Redis server address.
     */
    public static final String ADDRESS;

    /**
     * Static initialiser.
     */
    static {
        Map<String, String> config = PropertiesLoader.getProperties("db");
        ADDRESS = config.getOrDefault("address", "127.0.0.1:6379");
    }

    /**
     * Prevent instantiation.
     */
    private DbConfig() {}
}

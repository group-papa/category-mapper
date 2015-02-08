package uk.ac.cam.cl.retailcategorymapper.config;

import java.util.Map;

/**
 * Configuration options for the database.
 */
public final class DbConfig {
    /**
     * Redis host.
     */
    public static final String HOST;

    /**
     * Redis port.
     */
    public static final int PORT;

    /**
     * Static initialiser.
     */
    static {
        Map<String, String> config = PropertiesLoader.getProperties("db");
        HOST = config.getOrDefault("host", "127.0.0.1");
        PORT = Integer.parseInt(config.getOrDefault("port", "6379"));
    }

    /**
     * Prevent instantiation.
     */
    private DbConfig() {}
}

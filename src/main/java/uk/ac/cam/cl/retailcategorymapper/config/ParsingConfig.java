package uk.ac.cam.cl.retailcategorymapper.config;

import java.util.Map;

/**
 * Configuration options for parsing.
 */
public final class ParsingConfig {
    /**
     * Category file delimiter.
     */
    public static final String CATEGORYFILEDELIMITER;

    /**
     * Static initialiser.
     */
    static {
        Map<String, String> config = PropertiesLoader.getProperties("parsing");
        CATEGORYFILEDELIMITER = config.getOrDefault("categoryFileDelimiter", ">");
    }

    /**
     * Prevent instantiation.
     */
    private ParsingConfig() {}
}

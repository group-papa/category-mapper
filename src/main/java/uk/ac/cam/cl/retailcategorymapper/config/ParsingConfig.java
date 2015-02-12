package uk.ac.cam.cl.retailcategorymapper.config;

import java.util.Map;

/**
 * Configuration options for parsing.
 */
public final class ParsingConfig {
    /**
     * Category file delimiter.
     */
    public static final String CATEGORY_FILE_DELIMITER;

    /**
     * Static initialiser.
     */
    static {
        Map<String, String> config = PropertiesLoader.getProperties("parsing");
        CATEGORY_FILE_DELIMITER = config.getOrDefault("categoryFileDelimiter", ">");
    }

    /**
     * Prevent instantiation.
     */
    private ParsingConfig() {}
}

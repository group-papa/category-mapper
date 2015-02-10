package uk.ac.cam.cl.retailcategorymapper.utils;

import java.util.UUID;

/**
 * Utility class for generating random UUIDs.
 */
public class Uuid {
    public static String generateUUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}

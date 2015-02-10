package uk.ac.cam.cl.retailcategorymapper.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for dates and times.
 */
public class DateTime {
    private static final DateFormat ISO8601_UTC_FORMAT = new SimpleDateFormat
            ("yyyy-MM-dd'T'HH:mm:ss'Z'");

    public static String getCurrentTimeIso8601() {
        return ISO8601_UTC_FORMAT.format(new Date());
    }
}

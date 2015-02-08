package uk.ac.cam.cl.retailcategorymapper.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Load configuration from .properties files.
 */
public class PropertiesLoader {
    public static Map<String, String> getProperties(String name) {
        String filename = "config/" + name + ".properties";
        Properties properties = new Properties();
        InputStream input = null;
        Map<String, String> result = new HashMap<>();

        try {
            input = PropertiesLoader.class.getClassLoader()
                    .getResourceAsStream(filename);
            if (input == null) {
                return result;
            }

            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Enumeration<?> propertiesEnumeration = properties.propertyNames();
        while (propertiesEnumeration.hasMoreElements()) {
            String key = (String) propertiesEnumeration.nextElement();
            String value = properties.getProperty(key);
            result.put(key, value);
        }

        return result;
    }
}

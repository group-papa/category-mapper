package uk.ac.cam.cl.retailcategorymapper.classifier.features;

public class Sanitizer {
    public static String sanitize(String s) {
        if (s == null) {
            return "";
        }
        s = removePunctuation(s).toLowerCase().trim();
        return s;
    }

    public static String removePunctuation(String s) {
        return s.replaceAll("[^a-zA-Z0-9\\s]", " ").replaceAll("\\s\\s+", " ");
    }
}

package uk.ac.cam.cl.retailcategorymapper.classifier.features;

public class Sanitizer {

    public static final String[] uselessWords = new String[]{
            "an", "are","best", "in", "is", "it", "of", "or",
            "our", "out", "than", "the", "then", "your", "for",
            "as"};

    public static String sanitize(String s) {
        if (s == null) {
            return "";
        }
        s = removePunctuation(s);
        s = removeCapitals(s);
        s = removeUselessWords(s);
        s = removePunctuation(s);
        return s;
    }

    public static String removeUselessWords(String s){
        for(String uselessWord : uselessWords){
            s = s.replace(uselessWord+" ","");
        }
        return s;
    }

    public static String removeCapitals(String s) {
        return s.toLowerCase();
    }

    public static String removePunctuation(String s) {
        s = s.replace(")", "");
        s = s.replace("(", "");
        s = s.replace("/", "");
        s = s.replace("\\", "");
        s = s.replace("\"", "");
        s = s.replace("{", "");
        s = s.replace("}", "");
        s = s.replace("[", "");
        s = s.replace("]", "");
        s = s.replace(".", "");
        s = s.replace(",", "");
        s = s.replace("-", "");
        s = s.replace("!", "");
        s = s.replace(";", "");
        s = s.replace(":", "");
        s = s.replace("?", "");
        s = s.replace("|", "");
        s = s.replace("&", "");
        s = s.replace("£", "");
        s = s.replace("$", "");
        s = s.replace(">", "");
        s = s.replace("<", "");
        s = s.replace("  ", " ");
        return s;
    }
}

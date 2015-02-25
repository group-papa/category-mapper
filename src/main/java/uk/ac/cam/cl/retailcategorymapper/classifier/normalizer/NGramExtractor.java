package uk.ac.cam.cl.retailcategorymapper.classifier.normalizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NGramExtractor {
    /**
     * Returns a list of N-grams of the given length extracted from the given string.
     *
     * For example, given the string "one two three four" and length 2, returns
     * ("one two", "two three", "three four")
     *
     * @param input
     * @param nGramLength
     * @return
     */
    public static List<String> getNGrams(String input, int nGramLength) {
        String[] words = input.split("\\s+");
        if (nGramLength == 1) {
            return Arrays.asList(words);
        } else {
            List<String> ngrams = new ArrayList<String>(words.length);
            for (int i = 0; i < words.length - (nGramLength - 1); i++) {
                StringBuilder b = new StringBuilder();
                for (int j = 0; j < nGramLength; j++) {
                    if (j != 0) {
                        b.append(" ");
                    }
                    b.append(words[i+j]);
                }
                ngrams.add(b.toString());
            }
            return ngrams;
        }
    }

    public static void main(String[] args) {
        String input = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        int nGramLength = Integer.parseInt(args[0]);
        for (String w : NGramExtractor.getNGrams(input, nGramLength)) {
            System.out.println(w);
        }
    }
}

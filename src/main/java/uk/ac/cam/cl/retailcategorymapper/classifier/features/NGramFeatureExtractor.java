package uk.ac.cam.cl.retailcategorymapper.classifier.features;

import uk.ac.cam.cl.retailcategorymapper.classifier.normalizer.NGramExtractor;
import uk.ac.cam.cl.retailcategorymapper.classifier.normalizer.ProductNormalizer;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.FeatureSource;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.ArrayList;
import java.util.List;

public class NGramFeatureExtractor {

    static int MIN_NGRAM_LENGTH = 1;
    static int MAX_NGRAM_LENGTH = 3;

    private static List<Feature> generateNGramFeatures(FeatureSource source, String words, int
            minNGramLength, int maxNGramLength) {
        ArrayList<Feature> features = new ArrayList<>();
        words = ProductNormalizer.normalizeString(words);
        for (int ngramLength = minNGramLength; ngramLength <= maxNGramLength; ngramLength++) {
            for (String ngram : NGramExtractor.getNGrams(words, ngramLength)) {
                features.add(new Feature(source, ngram));
            }
        }
        return features;
    }

    public static List<Feature> changeProductToFeature(Product product) {
        return changeProductToFeature(product, MIN_NGRAM_LENGTH, MAX_NGRAM_LENGTH);
    }

    public static List<Feature> changeProductToFeature(Product product, int minNGramLength, int
            maxNGramLength) {
        Product normalizedProduct = ProductNormalizer.normalizeProduct(product);
        ArrayList<Feature> features = new ArrayList<Feature>();

        String name = normalizedProduct.getName();
        if (name != null && name.length() > 0) {
            features.addAll(generateNGramFeatures(FeatureSource.NAME, name, MIN_NGRAM_LENGTH,
                    MAX_NGRAM_LENGTH));
        }

        /*
         * FIXME: using description *decreases* accuracy?!
         *
         */
        String description = normalizedProduct.getDescription();
        if (description != null && description.length() > 0) {
            features.addAll(generateNGramFeatures(FeatureSource.DESCRIPTION, description,
                    MIN_NGRAM_LENGTH, MAX_NGRAM_LENGTH));
        }

        Category originalCategory = normalizedProduct.getOriginalCategory();
        String[] partsArray = originalCategory.getAllParts();
        if (partsArray != null && partsArray.length > 0) {
            String partsString = String.join(" ", partsArray);
            List<Feature> ngramFeatures = generateNGramFeatures(FeatureSource.ORIGINAL_CATEGORY,
                    partsString, minNGramLength, maxNGramLength);
            features.addAll(ngramFeatures);
        }

        return features;
    }
}

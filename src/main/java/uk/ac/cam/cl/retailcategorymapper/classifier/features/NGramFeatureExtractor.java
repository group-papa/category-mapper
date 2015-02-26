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

    private static List<Feature> generateNGramFeatures(FeatureSource source, String words, int maxNGramLength) {
        ArrayList<Feature> features = new ArrayList<>();
        //words = ProductNormalizer.normalizeString(words);
        words = Sanitizer1.sanitize(words);
        for (int ngramLength = MIN_NGRAM_LENGTH; ngramLength <= maxNGramLength; ngramLength++) {
            for (String ngram : NGramExtractor.getNGrams(words, ngramLength)) {
                features.add(new Feature(source, ngram));
            }
        }
        return features;
    }

    public static List<Feature> changeProductToFeature(Product product) {
        Product normalizedProduct = ProductNormalizer.normalizeProduct(product);
        ArrayList<Feature> features = new ArrayList<Feature>();

        String name = normalizedProduct.getName();
        if (name != null && name.length() > 0) {
            features.addAll(generateNGramFeatures(FeatureSource.NAME, name,MAX_NGRAM_LENGTH));
        }

        String description = normalizedProduct.getDescription();
        if (description != null && description.length() > 0) {
            features.addAll(generateNGramFeatures(FeatureSource.DESCRIPTION, description,MAX_NGRAM_LENGTH));
        }

        Integer priceInteger = normalizedProduct.getPrice();
        if ((priceInteger != null) && (priceInteger != -1)) {
            String price = Integer.toString(priceInteger);
            features.add(new Feature(FeatureSource.PRICE, price));
        }

        Category originalCategory = normalizedProduct.getOriginalCategory();
        String[] partsArray = originalCategory.getAllParts();
        if ((!(partsArray == null)) && (!(partsArray.length == 0))) {
            FeatureSource ft = FeatureSource.ORIGINAL_CATEGORY;
            for (int i = 0; i < partsArray.length; i++) {
                String categoryPart = partsArray[i];
                Feature cpFeature = new Feature(ft, categoryPart);
                features.add(cpFeature);
            }
        }

        return features;
    }
}

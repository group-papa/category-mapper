package uk.ac.cam.cl.retailcategorymapper.classifier.features;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.FeatureSource;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * taken the code from tatsianaivonchyk's implementation in NaiveBayesClassifier
 * feel free to get a better name
 */
public class FeatureConverter1 {
    public static List<Feature> changeProductToFeature(Product product) {
        List<Feature> createdFeatures = new ArrayList<>();

        String name = product.getName();
        if ((name != null) && !(name.equals(""))) {
            String[] splitName = name.split(" ");
            for (String s : splitName) {
                FeatureSource ft = FeatureSource.NAME;
                Feature nameFeature = new Feature(ft, s);
                createdFeatures.add(nameFeature);
            }
        }

        String description = product.getDescription();
        if ((description != null) && !(description.equals(""))) {
            String[] splitDescription = description.split(" ");
            for (String s : splitDescription) {
                FeatureSource ft = FeatureSource.DESCRIPTION;
                Feature descFeature = new Feature(ft, s);
                createdFeatures.add(descFeature);
            }
        }

        Integer priceInteger = product.getPrice();
        if ((priceInteger != null) && (priceInteger.intValue() != -1)) {
            String price = Integer.toString(product.getPrice());
            FeatureSource ft = FeatureSource.PRICE;
            Feature priceFeature = new Feature(ft, price);
            createdFeatures.add(priceFeature);
        }

        Category originalCategory = product.getOriginalCategory();
        String[] partsArray = originalCategory.getAllParts();
        if ((!(partsArray == null)) && (!(partsArray.length == 0))) {
            FeatureSource ft = FeatureSource.ORIGINAL_CATEGORY;
            for (String s : partsArray) {
                Feature cpFeature = new Feature(ft, s);
                createdFeatures.add(cpFeature);
            }
        }

        return createdFeatures;
    }
}

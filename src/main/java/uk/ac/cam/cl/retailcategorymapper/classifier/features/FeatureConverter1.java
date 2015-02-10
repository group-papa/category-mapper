package uk.ac.cam.cl.retailcategorymapper.classifier.features;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.ArrayList;
import java.util.List;

//import java.util.Map;

/*
 * taken the code from tatsianaivonchyk's implmenetation in NaieveBayesClassifier
 * feel free to get a better name
 */


public class FeatureConverter1 {

    public static List<Feature> changeProductToFeature(Product product) {
        List<Feature> createdFeatures = new ArrayList<Feature>();

        String name = product.getName();
        if ((name != null) && !(name.equals(""))) {
            FeatureSource ft = FeatureSource.NAME;
            Feature nameFeature = new Feature(ft, name);
            createdFeatures.add(nameFeature);
        }

        String description = product.getDescription();
        if ((description != null) && !(description.equals(""))) {
            FeatureSource ft = FeatureSource.DESCRIPTION;
            Feature descFeature = new Feature(ft, description);
            createdFeatures.add(descFeature);
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
            FeatureSource ft = FeatureSource.ORIGINALCATEGORY;
            for (int i = 0; i < partsArray.length; i++) {
                String categoryPart = partsArray[i];
                Feature cpFeature = new Feature(ft, categoryPart);
                createdFeatures.add(cpFeature);
            }
        }

        //not used right now!!!
        //Map<String, String> attributes = product.getAttributesMap();

        return createdFeatures;
    }

}

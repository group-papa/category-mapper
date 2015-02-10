package uk.ac.cam.cl.retailcategorymapper.feature;

import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

/*
 * taken the code from tatsianaivonchyk's implmenetation in NaieveBayesClassifier
 * feel free to get a better name 
 */


public class FeatureConverter1 {
	
	public static List<Feature> changeProductToFeature(Product product) {
		List<Feature> createdFeatures = new ArrayList<Feature>();

		String id = product.getId();
		if ((id != null) && !(id.equals(""))) {
			FeatureType ft = FeatureType.ID;
			Feature idFeature = new Feature(ft, id);
			createdFeatures.add(idFeature);
		}

		String name = product.getName();
		if ((name != null) && !(name.equals(""))) {
			FeatureType ft = FeatureType.NAME;
			Feature nameFeature = new Feature(ft, name);
			createdFeatures.add(nameFeature);
		}

		String description = product.getDescription();
		if ((description != null) && !(description.equals(""))) {
			FeatureType ft = FeatureType.DESCRIPTION;
			Feature descFeature = new Feature(ft, description);
			createdFeatures.add(descFeature);
		}

		Integer priceInteger = product.getPrice();
		if ((priceInteger != null) && (priceInteger.intValue() != -1)) {
			String price = Integer.toString(product.getPrice());
			FeatureType ft = FeatureType.PRICE;
			Feature priceFeature = new Feature(ft, price);
			createdFeatures.add(priceFeature);
		}

		Category originalCategory = product.getOriginalCategory();
		String[] partsArray = originalCategory.getAllParts();
		if ((!(partsArray == null)) && (!(partsArray.length == 0))) {
			FeatureType ft = FeatureType.ORIGINALCATEGORY;
			for (int i=0; i < partsArray.length; i++) {
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

package uk.ac.cam.cl.retailcategorymapper.classifier.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

/*
 * FeatureConverter2 differs from FeatureConverter1 as it does not use the
 * price or the description and splits up the words in the title (and filters
 * according to the loaded blacklist - just a file with one word per line)
 */

public class FeatureConverter2 {

	public static Set<String> blackList = new TreeSet<>();

	public static void loadBlackListFromFile(File f) {
		try {
			BufferedReader dataReader = new BufferedReader(new FileReader(f));
			String word = dataReader.readLine();

			while (word != null) {
				blackList.add(word);
				word = dataReader.readLine();
			}
			dataReader.close();
		} catch (FileNotFoundException e) {
			throw new RuntimeException("FileNotFound exception in loading blacklist in FeatureConverter2");
		} catch (IOException e) {
			throw new RuntimeException("I/O exception in loading blacklist in FeatureConverter2");
		}

	}

	public static List<Feature> changeProductToFeature(Product product) {
		List<Feature> createdFeatures = new ArrayList<Feature>();

		String name = product.getName();
		name = Sanitizer1.sanitize(name);
		String[] nameWords = name.split(" ");

		for (String s : nameWords) {
			if (s.equals("")) {continue;}
			if (!blackList.contains(s)) {
				createdFeatures.add(new Feature(FeatureSource.NAME, s));
			}
		}

		Category originalCategory = product.getOriginalCategory();
		String[] partsArray = originalCategory.getAllParts();
		if ((!(partsArray == null)) && (!(partsArray.length == 0))) {
			FeatureSource ft = FeatureSource.ORIGINAL_CATEGORY;
			for (int i=0; i < partsArray.length; i++) {
				String categoryPart = partsArray[i];
				Feature cpFeature = new Feature(ft, categoryPart);
				createdFeatures.add(cpFeature);
			}
		}

		return createdFeatures;
	}
}

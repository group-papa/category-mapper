package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Locale.Category;

/**
 * A Naive Bayes classifier implementation.
 */
public class NaiveBayesClassifier implements Classifier {
	private Set<Feature> featureSet;
	private Set<Category> categorySet;

	private Map<Feature, Integer> totalFeatureCounts; //count is across all categories
	private Map<Category, Integer> categoryCounts; // how many times has classifier encountered a category
	private Map<Category, Map<Feature, Integer>> featureCountPerCategory;

	public NaiveBayesClassifier<Feature,Category>() {

		this.featureSet = new HashSet<Feature>();
		this.categorySet = new HashSet<Category>();
		this.totalFeatureCounts = new HashMap<Feature, Integer>();
		this.categoryCounts = new HashMap<Category, Integer>();
		this.featureCountPerCategory = new HashMap<Category, Map<Feature, Integer>>();
	}

	public Set<Feature> getFeatures() {
		return this.featureSet;
	}

	public Set<Category> getCategories() {
		return this.categorySet;
	}
	
	public Map<Feature, Integer> getTotalFeatureCounts() {
		return this.totalFeatureCounts;
	}
	
	public Map<Category, Integer> getCategoryCounts() {
		return this.getCategoryCounts();
	}
	
	public Map<Category, Map<Feature, Integer>> getFeatureCountPerCategory() {
		return this.featureCountPerCategory;
	}

	/**
	 * return number of times the feature has been seen associated with given category
	 */
	public int getFeatureCountInCategory(Feature feature, Category category) {
		
		if (this.featureCountPerCategory.containsKey(category)) {
			Map<Feature, Integer> featureCounts = this.featureCountPerCategory.get(category);
			//feature seen associated with this category
			if (featureCounts.containsKey(feature)) {
				return featureCounts.get(feature);
			}
			//feature NOT seen associated with this category
			else {
				return 0;
			}
		}
		//category not seen associated with any features
		else {
			return 0;
		}
	}
	
	public double getFeatureProbabilityGivenCategory(Feature feature, Category category) {
		//have never seen this category
		if (!this.categorySet.contains(category)) {
			return 0;
		}
		//have seen this category: (# times seen feature in category)/(# times this category has come up = #total features in category)
		else {
			(double)this.getFeatureCountInCategory(feature, category) / (double)this.categoryCounts.get(category);
		}
	}
	
	/**
	 * returns the total number of times the classifier has seen any category. 
	 * Even if no feature is associated with this category still take it into consideration(???)
	 */
	public int getTotalCategoriesSeen() {
		int total = 0;
		Map<Category, Integer> catCounts = this.categoryCounts;
		Set<Category> categorySet = this.categorySet;
		Iterator<Category> itr = categorySet.iterator();
		while (itr.hasNext()) {
			Category c = itr.next();
			int cCount = catCounts.get(c);
			// ???
			if (cCount == 0) {
				total += 1;
			} 
			//at least one feature is associated with this category
			else {
				total += cCount;
			}
		}
		return total;
	}
	
	/**
	 * Probability not strictly based on the counts. A category in a taxonomy that had no products in
	 * it still has a small chance of being randomly chosen.
	 */
	public double getCategoryProbability(Category category) {
		if (!this.categorySet.contains(category)) {
			return 0;
		}
		else {
			int timesSeen = this.categoryCounts.get(category);
			//allow for a tiny chance 
			if (timesSeen == 0) {
				timesSeen = 1;
			}
			int totalTimesAnyCategorySeen = this.getTotalCategoriesSeen();
			return double(timesSeen) / ((double)totalTimesAnyCategorySeen);
		}
	}
	
	/**
	 * return number of times the category has been seen by the classifier
	 */
	public int getCategoryCount(Category category) {
		if (this.categoryCounts.containsKey(category)) {
			return this.categoryCounts.get(category);
		}
		else {
			return 0;
		}
	}

	/**
	 * Will be used for training? when we go through the training data and see a feature linked with a category,
	 *  call this method to add to the maps and sets.
	 *  
	 *  Does affect categoryCounts! 
	 */
	public void addSeenFeatureInSpecifiedCategory(Feature featureSeen, Category category) {
		this.featureSet.add(featureSeen);
		this.categorySet.add(category);
		
		if (this.categoryCounts.containsKey(categorySeen)) {
			int prevCount = this.categoryCounts.get(categorySeen);
			this.categoryCounts.put(categorySeen, prevCount+1);
		} 
		else {
			this.categoryCounts.put(categorySeen, 1);
		}

		//have seen this feature before
		if (this.totalFeatureCounts.containsKey(featureSeen)) { 
			int lastCount = this.totalFeatureCounts.get(featureSeen);
			this.totalFeatureCounts.put(featureSeen, lastCount+1); //update count
		} 
		// have never seen this feature before
		else { 
			this.totalFeatureCounts.put(featureSeen, 1);
		}
		
		//have previously seen this category associated with a feature
		if (this.featureCountPerCategory.containsKey(category)) { 
			Map<Feature, Integer> fCountInSpecifiedCategory = this.featureCountPerCategory.get(category);
			
			//update count of times feature is seen in given category:	
				//have seen feature before in this category
			if (fCountInSpecifiedCategory.containsKey(featureSeen)) {
				int prevCount = fCountInSpecifiedCategory.get(featureSeen);
				fCountInSpecifiedCategory.put(featureSeen, prevCount+1); 
			} 
				//first time seeing this feature in this category
			else {
				fCountInSpecifiedCategory.put(featureSeen, 1);
			}
			this.featureCountPerCategory.put(category, fCountInSpecifiedCategory);
		} 
		//first time seeing this category associated with a feature
		else { 
			Map<Feature, Integer> featureCountsInCategory = new HashMap<Feature, Integer>();
			featureCountsInCategory.put(featureSeen, 1);
			this.featureCountPerCategory.put(category, featureCountsInCategory);
		}
	}
	
	/**
	 * To be called on categories which are not in the set AFTER all calls to 
	 * addSeenFeatureInSpecifiedCategory have been made. This method sets the count
	 * of the category to 0 in the count map, but adds them to the set so they are included in 
	 * the total used to calculate the probability of a category (see getTotalCategoriesSeen()
	 * and getCategoryProbability()).
	 */
	public void addCategoryWithNoProducts(Category category) {
		this.categorySet.add(category);
		this.categoryCounts.put(category, 0);
	}
	
	/**
	 * Convert all details that a product object holds into separate features if not null
	 * or equal to the empty value as described in the product constructor javadoc.
	 * 
	 * Categories are treated as "bag of words" and the various levels of a category are not
	 * taken into consideration when mapping to features.
	 * 
	 * Currently does nothing with the Attributes Map in a Product.
	 */
	public Set<Feature> changeProductToFeature(Product product) {
		
		Set<Feature> createdFeatures = new HashSet<Feature>();
		
		String id = product.getId();
		if ((id != null) || !(id.equals(""))) {
			FeatureType ft = FeatureType.ID;
			Feature idFeature = new Feature(ft, id);
			createdFeatures.add(idFeature);
		}
		
		String name = product.getName();
		if ((name != null) || !(name.equals(""))) {
			FeatureType ft = FeatureType.NAME;
			Feature nameFeature = new Feature(ft, name);
			createdFeatures.add(nameFeature);
		}
		
		String description = product.getDescription();
		if ((description != null) || !(description.equals(""))) {
			FeatureType ft = FeatureType.DESCRIPTION;
			Feature descFeature = new Feature(ft, description);
			createdFeatures.add(descFeature);
		}
		
		Integer priceInteger = product.getPrice();
		if ((priceInteger != null) || (priceInteger.intValue() != -1)) {
			String price = Integer.toString(product.getPrice());
			FeatureType ft = FeatureType.PRICE;
			Feature priceFeature = new Feature(ft, price);
			createdFeatures.add(priceFeature);
		}
		
		Category originalCategory = product.getOriginalCategory();
		String[] partsArray = originalCategory.getAllParts();
		if ((!partsArray == null) || (!partsArray.length == 0)) {
			FeatureType ft = FeatureType.ORIGINALCATEGORY;
			for (int i=0; i < partsArray.length; i++) {
				String categoryPart = partsArray[i];
				Feature cpFeature = new Feature(ft, categoryPart);
				createdFeatures.add(cpFeature);
			}
		}
		
		//not used right now!!!
		Map<String, String> attributes = product.getAttributes();
	
	}
	
	/**
	 * Update the sets and maps held by the classifier which will be used for training with
	 * information from a whole list of products.
	 */
	public void trainWithBagOfWordsListProducts(Taxonomy taxonomy, List<Product> inputProductsList) {
		Set<Feature> featuresCreatedSet = this.changeProductToFeature(product);
		//iterate through all products  
		for (Product product : inputProductsList) {
			Category originalCategory = product.getOriginalCategory();
			
			//add features from this product to the featuresCreatedSet
			Iterator<Feature> itr = featuresCreatedSet.iterator();
			while (itr.hasNext()) {
				Feature feature = itr.next();
				this.addSeenFeatureInSpecifiedCategory(feature, originalCategory);
			}	
		}
		
		//check if any categories remain that had no products in them -- want to add them separately
		List<Category> categoriesList = taxonomy.getCategories();
		for (Category c : categoriesList) {
			if (!this.categorySet.contains(c)) {
				this.addCategoryWithNoProducts(c);
			}
		}
	}
	
	public void trainWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight,
    		double destinationCategoryWeight) {
		
		throw new UnsupportedOperationException();
		
	}
	
	
	public Mapping classifyWithBagOfWords(Taxonomy taxonomy, Product product) {
		throw new UnsupportedOperationException();
	}
	
	
	
	
	
	public Mapping classifyWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight) {
		
		throw new UnsupportedOperationException();
	}
	
	
	public Mapping classify(Taxonomy taxonomy, Product product) {
		
		throw new UnsupportedOperationException();
	}
}

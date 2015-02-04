package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A Naive Bayes classifier implementation.
 */
public class NaiveBayesClassifier<F, C> implements Classifier {
	private Set<F> featureSet;
	private Set<C> categorySet;

	private Map<F, Integer> totalFeatureCounts; //count is across all categories
	private Map<C, Integer> categoryCounts; // how many times has classifier encountered a category
	private Map<C, Map<F, Integer>> featureCountPerCategory;

	public NaiveBayesClassifier<F,C>() {

		this.featureSet = new HashSet<F>();
		this.categorySet = new HashSet<C>();
		this.totalFeatureCounts = new HashMap<F, Integer>();
		this.categoryCounts = new HashMap<C, Integer>();
		this.featureCountPerCategory = new HashMap<C, Map<F, Integer>>();
	}

	public Set<F> getFeatures() {
		return this.featureSet;
	}

	public Set<C> getCategories() {
		return this.categorySet;
	}
	
	public Map<F, Integer> getTotalFeatureCounts() {
		return this.totalFeatureCounts;
	}
	
	public Map<C, Integer> getCategoryCounts() {
		return this.getCategoryCounts();
	}
	
	public Map<C, Map<F, Integer>> getFeatureCountPerCategory() {
		return this.featureCountPerCategory;
	}

	/**
	 * return number of times the feature has been seen associated with given category
	 */
	public int getFeatureCountInCategory(F feature, C category) {
		
		if (this.featureCountPerCategory.containsKey(category)) {
			Map<F, Integer> featureCounts = this.featureCountPerCategory.get(category);
			//feature seen associated with this category
			if (featureCounts.containsKey(feature)) {
				return featureCounts.get(feature);
			}
			//feature NOT seen associated with this category
			else {
				return 0;
			}
		}
		//category not seen
		else {
			return 0;
		}
	}
	
	public double getFeatureProbabilityGivenCategory(F feature, C category) {
		//have never seen this category
		if (!this.categoryCounts.containsKey(category)) {
			return 0;
		}
		//have seen this category: (# times seen feature in category)/(# times this category has come up = #total features in category)
		else {
			(double)this.getFeatureCountInCategory(feature, category) / (double)this.categoryCounts.get(category);
		}
	}
	
	/**
	 * returns the total number of times the classifier has seen any category
	 */
	public int getTotalCategoriesSeen() {
		Map<C, Integer> catCounts = this.categoryCounts;
		int total = 0;
		//add up all counts of all categories
		for (C category : catCounts.keySet()) {
			total += catCounts.get(category);
		}
		return total;
	}
	
	public double getCategoryProbability(C category) {
		if (!this.categorySet.contains(category)) {
			return 0;
		}
		else {
			int timesSeen = this.categoryCounts.get(category);
			int totalTimesAnyCategorySeen = this.getTotalCategoriesSeen();
			return double(timesSeen) / ((double)totalTimesAnyCategorySeen);
		}
	}
	
	/**
	 * return number of times the category has been seen by the classifier
	 */
	public int getCategoryCount(C category) {
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
	public void addSeenFeatureInSpecifiedCategory(F featureSeen, C category) {
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
			Map<F, Integer> fCountInSpecifiedCategory = this.featureCountPerCategory.get(category);
			
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
			Map<F, Integer> featureCountsInCategory = new HashMap<F, Integer>();
			featureCountsInCategory.put(featureSeen, 1);
			this.featureCountPerCategory.put(category, featureCountsInCategory);
		}
	}
	
	/**
	 * Convert all details that a product object holds into separate features if not null
	 * or equal to the empty value as described in the product constructor javadoc.
	 * 
	 * Categories are treated as "bag of words" and the various levels of a category are not
	 * taken into consideration when mapping to features.
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
		
		
		Map<String, String> attributes = product.getAttributes();
		
		
	}
	
	/**
	 * Update the sets and maps held by the classifier which will be used for training
	 */
	@Override
	public void trainWithBagOfWords(Taxonomy taxonomy, Product product) {
		
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void trainWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight,
    		double destinationCategoryWeight) {
		
		throw new UnsupportedOperationException();
		
	}
	

	@Override
	public Mapping classifyWithBagOfWords(Taxonomy taxonomy, Product product) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Mapping classifyWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight) {
		throw new UnsupportedOperationException();
	}
}

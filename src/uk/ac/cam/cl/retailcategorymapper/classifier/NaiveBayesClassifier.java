package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * A Naive Bayes classifier implementation.
 */
public class NaiveBayesClassifier implements Classifier {
	private Set<Feature> featureSet;
	private Set<Category> categorySet;

	private Map<Feature, Integer> totalFeatureCounts; //count is across all categories
	private Map<Category, Integer> categoryCounts; // how many times has classifier encountered a category
	private Map<Category, Map<Feature, Integer>> featureCountPerCategory;

	public NaiveBayesClassifier() {

		this.featureSet = new HashSet<Feature>();
		this.categorySet = new HashSet<Category>();
		this.totalFeatureCounts = new HashMap<Feature, Integer>();
		this.categoryCounts = new HashMap<Category, Integer>();
		this.featureCountPerCategory = new HashMap<Category, Map<Feature, Integer>>();
	}

	/**
	 * Getter method for set of features seen by classifier in training.
	 * @return set of features seen by classifier in training
	 */
	public Set<Feature> getFeatures() {
		return this.featureSet;
	}

	/**
	 * Getter method for set of categories seen by classifier in training.
	 * @return set of categories seen by classifier in training
	 */
	public Set<Category> getCategories() {
		return this.categorySet;
	}

	/**
	 * Getter method for map of features to feature counts as seen in training.
	 * @return map of features to feature counts seen in training
	 */
	public Map<Feature, Integer> getTotalFeatureCounts() {
		return this.totalFeatureCounts;
	}

	/**
	 * Getter method for map of categories to count of number of times a category was seen in training.
	 * @return map of categories to number of times a category was seen in training
	 */
	public Map<Category, Integer> getCategoryCounts() {
		return this.getCategoryCounts();
	}

	/**
	 * Getter method for the Map of Category -> (Feature -> Integer).
	 * @return map associating categories to map of feature counts
	 */
	public Map<Category, Map<Feature, Integer>> getFeatureCountPerCategory() {
		return this.featureCountPerCategory;
	}


	/**
	 * Return the number of times a feature is seen in a category.
	 * 
	 * @param feature Feature we want to find the number of occurrences for
	 * @param category Category containing products that potentially produced the feature that we are
	 * interested in
	 * @return count of how many times a feature occurs in a given category from potentially
	 *  multiple products inside the category
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

	/**
	 * Calculate the probability that a feature is seen given the category. This represents 
	 * P(F|C) in the Bayes equation.
	 * 
	 * @param feature Feature to calculate the probability of
	 * @param category Category given
	 * @return probability that feature is seen given the category
	 */
	public double getFeatureProbabilityGivenCategory(Feature feature, Category category) {
		//have never seen this category
		if (!this.categorySet.contains(category)) {
			return 0;
		}
		//have seen this category: (# times seen feature in category)/(# times this category has come up = #total features in category)
		else {
			return ((double)this.getFeatureCountInCategory(feature, category)) / ((double)this.categoryCounts.get(category));
		}
	}

	/**
	 * Return the total number of times the classifier has seen any category. This number will only
	 * include the categories that the classifier has seen through a product/feature from within the 
	 * category. Any empty categories will not be counted here.
	 * 
	 * @return total number of times that the classifier has encountered a category
	 */
	public int getTotalCategoriesSeen() {
		int total = 0;
		Map<Category, Integer> catCounts = this.categoryCounts;
		Set<Category> categorySet = this.categorySet;
		Iterator<Category> itr = categorySet.iterator();
		while (itr.hasNext()) {
			Category c = itr.next();
			int cCount = catCounts.get(c);
			total += cCount;
		}
		return total;
	}

	/**
	 * Return the probability that a Category is seen by a classifier based on training data. 
	 * This represents P(C) in Bayes equation.
	 * 
	 * @param category Category to calculate the probability for
	 * @return number representing probability of that category
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
			return ((double)timesSeen) / ((double)totalTimesAnyCategorySeen);
		}
	}

	/**
	 * Return the number of times the category has been seen by the classifier.
	 * 
	 * @param category Category we want to find out the number of occurrences for
	 * @return number of times that the classifier has come across a category
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
	 * When we go through the training data and see a feature linked with a category, 
	 * call this method to add to the maps and sets of Naive Bayes Classifier. 
	 * 
	 * @param featureSeen Feature from product in training set
	 * @param category Destination category that the product which feature is derived from is put into
	 */
	public void addSeenFeatureInSpecifiedCategory(Feature featureSeen, Category category) {
		this.featureSet.add(featureSeen);
		this.categorySet.add(category);

		//update categoryCounts
		//have seen category before
		if (this.categoryCounts.containsKey(category)) {
			int prevCount = this.categoryCounts.get(category);
			this.categoryCounts.put(category, prevCount+1);
		} 
		//have NOT seen category before
		else {
			this.categoryCounts.put(category, 1);
		}

		//update totalFeatureCounts
		//have seen this feature before
		if (this.totalFeatureCounts.containsKey(featureSeen)) { 
			int prevCount = this.totalFeatureCounts.get(featureSeen);
			this.totalFeatureCounts.put(featureSeen, prevCount+1); //update count
		} 
		// have NOT seen this feature before
		else { 
			this.totalFeatureCounts.put(featureSeen, 1);
		}

		//update Category -> (Feature -> Integer) Map
		//have previously seen this category associated with a feature
		if (this.featureCountPerCategory.containsKey(category)) { 
			Map<Feature, Integer> fCountInSpecifiedCategory = this.featureCountPerCategory.get(category);
			//update count of times feature is seen in given category (inner map)
			//have seen feature before in this category
			if (fCountInSpecifiedCategory.containsKey(featureSeen)) {
				int prevCount = fCountInSpecifiedCategory.get(featureSeen);
				fCountInSpecifiedCategory.put(featureSeen, prevCount+1); 
			} 
			//have NOT seen this feature before in this category
			else {
				fCountInSpecifiedCategory.put(featureSeen, 1);
			}
			//update outer layer of map
			this.featureCountPerCategory.put(category, fCountInSpecifiedCategory);
		} 
		//have NOT see this category associated with a feature before
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
	 * 
	 * @param category Category with no products in it
	 */
	public void addCategoryWithNoProducts(Category category) {
		this.categorySet.add(category);
		this.categoryCounts.put(category, 0);
	}

	/**
	 * Convert all details that a product object holds into separate features if not null
	 * or equal to the empty value as described in the product constructor javadoc. 
	 * Categories are treated as "bag of words" and the various levels of a category are not
	 * taken into consideration when mapping to features. Currently does nothing with the 
	 * Attributes Map in a Product.
	 * 
	 * @param product Product used to extract features from
	 * @return list of features from the input product
	 */
	public List<Feature> changeProductToFeature(Product product) {
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
		Map<String, String> attributes = product.getAttributesMap();

		return createdFeatures;
	}

	/**
	 * Update the sets and maps held by the classifier which will be used for training with
	 * information from a product.
	 * 
	 * @param product Product that has been classified
	 * @param category Destination category that product has been mapped to
	 */
	public void trainWithBagOfWordsSingleProduct(Product product, Category category) {
		List<Feature> featuresFromProduct = this.changeProductToFeature(product);

		for (Feature f : featuresFromProduct) {
			this.addSeenFeatureInSpecifiedCategory(f, category);
		}	
	}


	/**
	 * Generate a mapping for a product given the destination taxonomy. 
	 * Use Laplace Smoothing to take into account any categories or features not seen
	 * so that the product never results in a value of 0.
	 * 
	 * @param taxonomy Destination taxonomy containing the categories mapping to.
	 * @param product Product we are mapping into the destination taxonomy
	 * @return mapping of the product into the destination taxonomy
	 */
	public Mapping classifyWithBagOfWords(Taxonomy taxonomy, Product product) {
		//treemap sorts in increasing order
		NavigableMap<Double, Category> probabilityToAllPossibleCategories = new TreeMap<Double,Category>();
		
		List<Feature> features = this.changeProductToFeature(product);
		List<Category> allDestinationCategories = taxonomy.getCategories();
		
		//take a single category
		for (Category category : allDestinationCategories) {
			//calculate P(f_i | C)
			double pProductGivenC = 1.0;
			double pFeatureGivenC = 1.0;
			
			//category has been seen by classifier during training
			if (this.categorySet.contains(category)) {
				Map<Feature, Integer> featureOccurrencesInCategory = this.featureCountPerCategory.get(category);
				for (Feature f : features) {
					//assume f has NOT been seen in this category
					int count = 0;
					//update if it has been seen in this category
					if (featureOccurrencesInCategory.containsKey(f)) {
						count = featureOccurrencesInCategory.get(f);
					}
					//Laplace smoothing
					pFeatureGivenC = ((double)(1 + count)) / ((double)(this.categoryCounts.get(category) + this.featureSet.size()));
				}
			}
			//category has not been seen by the classifier during training ?!?!
			else {
				
			}
			pProductGivenC *= pFeatureGivenC;

			
			double pC = 1.0;
			//category seen by classifier during training
			if (this.categorySet.contains(category)) {
				int catCount = this.getCategoryCount(category);
				int totalSeenCategoryCount = this.getTotalCategoriesSeen();
				int numberAllCategories = allDestinationCategories.size();
				//Laplace smoothing
				pC *= ((double)(catCount +1)) / ((double)(totalSeenCategoryCount + numberAllCategories));
			}
			//category NOT seen by classifier during training
			else {
				
			}
			double pCGivenF = pProductGivenC * pC;
			probabilityToAllPossibleCategories.put(pCGivenF, category);
		}
		Category mostLikelyCategory = probabilityToAllPossibleCategories.lastEntry().getValue();
		MappingBuilder mb = new MappingBuilder();
		mb.setCategory(mostLikelyCategory);
		mb.setProduct(product);
		mb.setMethod(Method.CLASSIFIED);
		//mb.setConfidence()    STILL TO BE IMPLEMENTED
		Mapping finalMapping = mb.createMapping();
		return finalMapping;
	}


	public void trainWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
			double nameWeight, double descriptionWeight, double priceWeight,
			double destinationCategoryWeight) {	
		throw new UnsupportedOperationException();	
	}

	public Mapping classifyWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
			double nameWeight, double descriptionWeight, double priceWeight) {		
		throw new UnsupportedOperationException();
	}


	@Override
	public Mapping classify(Taxonomy taxonomy, Product product) {
		throw new UnsupportedOperationException();
	}
}

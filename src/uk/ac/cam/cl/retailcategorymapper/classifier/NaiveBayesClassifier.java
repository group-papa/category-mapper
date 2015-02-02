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

	private Map<F, Integer> totalFeatureCount; //count is across all categories
	private Map<C, Integer> categoryCount;
	private Map<C, Map<F, Integer>> featureCountPerCategory;

	public NaiveBayesClassifier() {

		this.featureSet = new HashSet<F>();
		this.categorySet = new HashSet<C>();
		this.totalFeatureCount = new HashMap<F, Integer>();
		this.categoryCount = new HashMap<C, Integer>();
		this.featureCountPerCategory = new HashMap<C, Map<F, Integer>>();

	}

	public Set<F> getFeatures() {
		return this.featureSet;
	}

	public Set<C> getCategories() {
		return this.categorySet;
	}

	public void addCategory(C categorySeen) {
		this.categorySet.add(categorySeen); 

	}

	/**
	 * Method to add several categories at once
	 */
	public void addCollectionCategories(Collection<C> categories) {
		this.categorySet.addAll(categories);
	}

	/**
	 * Will be used for training? when we go through the training data and see a feature linked with a category,
	 *  call this method to add to the maps and sets
	 */
	public void addSeenFeatureInSpecifiedCategory(F featureSeen, C category) {
		this.featureSet.add(featureSeen);
		this.categorySet.add(category);

		//have seen this feature before
		if (this.totalFeatureCount.containsKey(featureSeen)) { 
			int lastCount = this.totalFeatureCount.get(featureSeen);
			this.totalFeatureCount.put(featureSeen, lastCount+1); //update count
		} 
		// have never seen this feature before
		else { 
			this.totalFeatureCount.put(featureSeen, 1);
		}
		
		//have previously seen this category
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
		//first time seeing this category
		else { 
			Map<F, Integer> featureCountsInCategory = new HashMap<F, Integer>();
			featureCountsInCategory.put(featureSeen, 1);
			this.featureCountPerCategory.put(category, featureCountsInCategory);
		}
	}

	@Override
	public Mapping classify(Taxonomy taxonomy, Product product) {
		throw new UnsupportedOperationException();
	}
}

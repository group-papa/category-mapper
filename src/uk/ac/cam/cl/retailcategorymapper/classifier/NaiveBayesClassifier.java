package uk.ac.cam.cl.retailcategorymapper.classifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class NaiveBayesClassifier<F, C> {
	
	private Set<F> featureSet;
	private Set<C> categorySet;
	
	private Map<C, HashMap<F, Integer>> featureCountPerCategory;
	private Map<F, Integer> numberOfFeatureOccurrences;
	
	public NaiveBayesClassifier() {
		this.featureCountPerCategory = new HashMap<C, HashMap<F, Integer>>();
		this.numberOfFeatureOccurrences = new HashMap<F, Integer>();	
	}
	
	public Set<F> getFeatures() {
		return this.featureSet;
	}
	
	public Set<C> getCategories() {
		return this.categorySet;
	}
	
	public void addSeenFeature(F featureSeen, C category) {
		Map<F, Integer> featureCountMap = this.numberOfFeatureOccurrences;
		
	}
	
}

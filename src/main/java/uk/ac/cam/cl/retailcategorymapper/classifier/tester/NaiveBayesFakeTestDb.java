package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import org.apache.commons.lang3.tuple.Pair;
import org.redisson.Redisson;
import org.redisson.core.RBucket;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesStorage;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Methods for persisting values required by the Naive Bayes Classifier.
 */
public class NaiveBayesFakeTestDb implements NaiveBayesStorage {

	private static NaiveBayesFakeTestDb instance = null;

	private Map<Taxonomy, Integer> totalProductCounts;
	private Map<Taxonomy, Map<Category, Integer>> categoryProductCounts;
	private Map<Taxonomy, Map<Category, Integer>> categoryFeatureCounts;
	private Map<Taxonomy, Map<Category, Map<Feature, Integer>>> categoryObservations;
	private Map<Taxonomy, Set<Feature>> featureSets;

	private NaiveBayesFakeTestDb() {
		totalProductCounts = new HashMap<>();
		categoryProductCounts = new HashMap<>();
		categoryFeatureCounts = new HashMap<>();
		categoryObservations = new HashMap<>();
		featureSets = new HashMap<>();
	}

	public static NaiveBayesFakeTestDb getInstance() {
		if (instance == null) instance = new NaiveBayesFakeTestDb();
		return instance;
	}

	/**
	 * Get the count of total products seen in a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @return The product count.
	 */
	public int getProductCount(Taxonomy taxonomy) {
		return totalProductCounts.getOrDefault(taxonomy, 0);
	}

	/**
	 * Set the count of total products seen in a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @param count The new count.
	 */
	public void setProductCount(Taxonomy taxonomy, int count) {
		totalProductCounts.put(taxonomy, count);
	}

	/**
	 * Get a category to product count map for a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @return The category product count map.
	 */
	public Map<Category, Integer> getCategoryProductMap(Taxonomy taxonomy) {
		if (!categoryProductCounts.containsKey(taxonomy)) {
			categoryProductCounts.put(taxonomy, new HashMap<>());
		}
		return categoryProductCounts.get(taxonomy);
	}

	/**
	 * Get a category to feature count map for a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @return The category feature count map.
	 */
	public Map<Category, Integer> getCategoryFeatureMap(Taxonomy taxonomy) {
		if (!categoryFeatureCounts.containsKey(taxonomy)) {
			categoryFeatureCounts.put(taxonomy, new HashMap<>());
		}
		return categoryFeatureCounts.get(taxonomy);
	}

	/**
	 * Get a category's feature to observation count map for a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @param category The category.
	 * @return The category feature observation count map.
	 */
	public Map<Feature, Integer> getCategoryFeatureObservationMap(Taxonomy taxonomy,
	                                                              Category category) {
		if (!categoryObservations.containsKey(taxonomy)) {
			categoryObservations.put(taxonomy, new HashMap<>());
		}
		Map<Category, Map<Feature, Integer>> subMap = categoryObservations.get(taxonomy);
		if (!subMap.containsKey(category)) {
			subMap.put(category, new HashMap<>());
		}
		return subMap.get(category);
	}

	/**
	 * Get a feature set for a taxonomy.
	 * @param taxonomy The taxonomy.
	 * @return The feature set.
	 */
	public Set<Feature> getFeatureSet(Taxonomy taxonomy) {
		if (!featureSets.containsKey(taxonomy)) {
			featureSets.put(taxonomy, new HashSet<>());
		}
		return featureSets.get(taxonomy);
	}
}

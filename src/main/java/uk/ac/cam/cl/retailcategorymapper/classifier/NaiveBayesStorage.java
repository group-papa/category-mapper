package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Map;
import java.util.Set;

public interface NaiveBayesStorage {

    /**
     * Get the count of total products seen in a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @return The product count.
     */
    public int getProductCount(Taxonomy taxonomy);

    /**
     * Set the count of total products seen in a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @param count    The new count.
     */
    public void setProductCount(Taxonomy taxonomy, int count);

    /**
     * Get a category to product count map for a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @return The category product count map.
     */
    public Map<Category, Integer> getCategoryProductMap(Taxonomy taxonomy);

    /**
     * Get a category to feature count map for a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @return The category feature count map.
     */
    public Map<Category, Integer> getCategoryFeatureMap(Taxonomy taxonomy);

    /**
     * Get a category's feature to observation count map for a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @param category The category.
     * @return The category feature observation count map.
     */
    public Map<Feature, Integer> getCategoryFeatureObservationMap(Taxonomy taxonomy, Category
            category);

    /**
     * Get a feature set for a taxonomy.
     *
     * @param taxonomy The taxonomy.
     * @return The feature set.
     */
    public Set<Feature> getFeatureSet(Taxonomy taxonomy);

    public Map<Category, Taxonomy> getCategorySubTaxonomyMap(Taxonomy taxonomy);
}

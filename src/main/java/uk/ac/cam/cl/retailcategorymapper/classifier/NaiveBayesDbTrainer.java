package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.classifier.features.NGramFeatureExtractor;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Naive Bayes trainer implementation which integrates with the database.
 */
public class NaiveBayesDbTrainer extends Trainer {
    private Set<Feature> newTaxonomyFeatureSet;

    private Map<Category, Integer> categoryProductCount;
    private Map<Category, Integer> updatedCategoryProductCount;

    private Map<Category, Integer> categoryFeatureCount;
    private Map<Category, Integer> updatedCategoryFeatureCount;

    private Set<Category> destinationCategories;

    private Map<Category, Map<Feature, Integer>> categoryFeatureObservationMaps;
    private Map<Category, Map<Feature, Integer>> updatedCategoryFeatureObservationMaps;

    private int newProductsSeen;

    private NaiveBayesStorage storage;


    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public NaiveBayesDbTrainer(Taxonomy taxonomy, NaiveBayesStorage storage) {
        super(taxonomy);
        this.storage = storage;
        newTaxonomyFeatureSet = new HashSet<>();

        categoryProductCount = new HashMap<>(
                storage.getCategoryProductMap(taxonomy));
        updatedCategoryProductCount = new HashMap<>();

        categoryFeatureCount = new HashMap<>(
                storage.getCategoryFeatureMap(taxonomy));
        updatedCategoryFeatureCount = new HashMap<>();

        destinationCategories = new HashSet<>(taxonomy.getCategories());

        categoryFeatureObservationMaps = new HashMap<>();
        updatedCategoryFeatureObservationMaps = new HashMap<>();

        newProductsSeen = 0;
    }

    public NaiveBayesDbTrainer(Taxonomy taxonomy) {
        this(taxonomy, NaiveBayesDb.getInstance());
    }

    /**
     * Update the sets and maps held by the classifier which will be used for training with
     * information from a product.
     *
     * @param mapping The mapping to train with.
     */
    @Override
    public boolean train(Mapping mapping) {
        Product product = mapping.getProduct();
        Category category = mapping.getCategory();

        if (!destinationCategories.contains(category)) {
            return false;
        }

        List<Feature> featuresFromProduct = NGramFeatureExtractor.changeProductToFeature(product);

        newProductsSeen += 1;

        // Have seen category before
        if (categoryProductCount.containsKey(category)) {
            int prevCount = updatedCategoryProductCount.containsKey(category)
                    ? updatedCategoryProductCount.get(category)
                    : categoryProductCount.get(category);
            updatedCategoryProductCount.put(category, prevCount + 1);
        }
        // Have not seen category before in training
        else {
            updatedCategoryProductCount.put(category, 1);
        }

        for (Feature feature : featuresFromProduct) {
            this.addSeenFeatureInSpecifiedCategory(feature, category);
        }

        return true;
    }

    public boolean addFeatureInCategory(Feature feature, Category category){

        newProductsSeen += 1;
        // Have seen category before
        if (categoryProductCount.containsKey(category)) {
            int prevCount = updatedCategoryProductCount.containsKey(category)
                    ? updatedCategoryProductCount.get(category)
                    : categoryProductCount.get(category);
            updatedCategoryProductCount.put(category, prevCount + 1);
        }
        // Have not seen category before in training
        else {
            updatedCategoryProductCount.put(category, 1);
        }
        this.addSeenFeatureInSpecifiedCategory(feature, category);

        return true;
    }

    /**
     * Save the new training data.
     */
    @Override
    public void save() {
        storage.getFeatureSet(getTaxonomy()).addAll(newTaxonomyFeatureSet);
        newTaxonomyFeatureSet.clear();

        storage.getCategoryProductMap(getTaxonomy())
                .putAll(updatedCategoryProductCount);
        categoryProductCount.putAll(updatedCategoryProductCount);
        updatedCategoryProductCount.clear();

        storage.getCategoryFeatureMap(getTaxonomy())
                .putAll(updatedCategoryFeatureCount);
        categoryFeatureCount.putAll(updatedCategoryFeatureCount);
        updatedCategoryFeatureCount.clear();

        for (Map.Entry<Category, Map<Feature, Integer>> categoryMapEntry :
                updatedCategoryFeatureObservationMaps.entrySet()) {
            storage.getCategoryFeatureObservationMap(getTaxonomy(),
                    categoryMapEntry.getKey()).putAll(categoryMapEntry.getValue());
        }
        categoryFeatureObservationMaps.clear();
        updatedCategoryFeatureObservationMaps.clear();

        storage.setProductCount(getTaxonomy(),
                storage.getProductCount(getTaxonomy()) +
                        newProductsSeen);
        newProductsSeen = 0;
    }

    /**
     * When we go through the training data and see a features linked with a category,
     * call this method to add to the maps and sets of Naive Bayes Classifier.
     *
     * @param featureSeen Feature from product in training set
     * @param category    Destination category that the product which features is derived from is put into
     */
    private void addSeenFeatureInSpecifiedCategory(Feature featureSeen,
                                                   Category category) {
        //add to feature set
        newTaxonomyFeatureSet.add(featureSeen);

        //update categoryCounts based on features:
        //have seen category before associated with a feature
        if (categoryFeatureCount.containsKey(category)) {
            int prevCount = updatedCategoryFeatureCount.containsKey(category)
                    ? updatedCategoryFeatureCount.get(category)
                    : categoryFeatureCount.get(category);
            updatedCategoryFeatureCount.put(category, prevCount + 1);
        }
        //have NOT seen category before
        else {
            updatedCategoryFeatureCount.put(category, 1);
        }

        //update count of times specific features is seen in given category:
        if (!categoryFeatureObservationMaps.containsKey(category)) {
            categoryFeatureObservationMaps.put(category,
                    new HashMap<>(storage.getCategoryFeatureObservationMap(
                            getTaxonomy(), category)));
        }

        //have seen feature before in this category
        Map<Feature, Integer> categoryFeatureObservationMap =
                categoryFeatureObservationMaps.get(category);
        Map<Feature, Integer> updatedCategoryFeatureObservationMap =
                updatedCategoryFeatureObservationMaps.containsKey(category)
                        ? updatedCategoryFeatureObservationMaps.get(category)
                        : new HashMap<>();
        if (categoryFeatureObservationMap.containsKey(featureSeen)) {
            int prevCount = updatedCategoryFeatureObservationMap.containsKey(featureSeen)
                    ? updatedCategoryFeatureObservationMap.get(featureSeen)
                    : categoryFeatureObservationMap.get(featureSeen);
            updatedCategoryFeatureObservationMap.put(featureSeen, prevCount + 1);
        }
        //have NOT seen this features before in this category
        else {
            updatedCategoryFeatureObservationMap.put(featureSeen, 1);
        }
        updatedCategoryFeatureObservationMaps.put(category,
                updatedCategoryFeatureObservationMap);
    }
}

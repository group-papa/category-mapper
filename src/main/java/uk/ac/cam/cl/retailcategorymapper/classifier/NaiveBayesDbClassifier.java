package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.classifier.features.FeatureConverter1;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

/**
 * A Naive Bayes classifier implementation which integrates with the database.
 */
public class NaiveBayesDbClassifier extends Classifier {
    private Set<Feature> featureSet;
    private Map<Category, Integer> categoryProductMap;
    private Map<Category, Integer> categoryFeatureMap;
    private Set<Category> allDestinationCategories;

    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public NaiveBayesDbClassifier(Taxonomy taxonomy) {
        super(taxonomy);
        featureSet = NaiveBayesDb.getFeatureSet(taxonomy);
        categoryProductMap = NaiveBayesDb.getCategoryProductMap(taxonomy);
        categoryFeatureMap = NaiveBayesDb.getCategoryFeatureMap(taxonomy);
        allDestinationCategories = taxonomy.getCategories();
    }

    /**
     * When we go through the training data and see a features linked with a category,
     * call this method to add to the maps and sets of Naive Bayes Classifier.
     *
     * @param featureSeen Feature from product in training set
     * @param category    Destination category that the product which features is derived from is put into
     */
    public void addSeenFeatureInSpecifiedCategory(Feature featureSeen, Category category) {
        //add to feature set
        featureSet.add(featureSeen);

        //update categoryCounts based on features:
        //have seen category before associated with a feature
        if (categoryFeatureMap.containsKey(category)) {
            int prevCount = categoryFeatureMap.get(category);
            categoryFeatureMap.put(category, prevCount + 1);
        }
        //have NOT seen category before
        else {
            categoryFeatureMap.put(category, 1);
        }

        //update count of times specific features is seen in given category:

        //have seen feature before in this category
        Map<Feature, Integer> categoryFeatureObservationMap = NaiveBayesDb
                .getCategoryFeatureObservationMap(getTaxonomy(), category);
        if (categoryFeatureObservationMap.containsKey(featureSeen)) {
            int prevCount = categoryFeatureObservationMap.get(featureSeen);
            categoryFeatureObservationMap.put(featureSeen, prevCount + 1);
        }
        //have NOT seen this features before in this category
        else {
            categoryFeatureObservationMap.put(featureSeen, 1);
        }
    }

    /**
     * Update the sets and maps held by the classifier which will be used for training with
     * information from a product.
     *
     * @param product  Product that has been classified
     * @param category Destination category that product has been mapped to
     */
    public void trainWithBagOfWordsSingleProduct(Product product, Category category) {
        List<Feature> featuresFromProduct = FeatureConverter1.changeProductToFeature(product);

        NaiveBayesDb.incrementProductCount(getTaxonomy());

        //have seen category before
        if (categoryProductMap.containsKey(category)) {
            int prevCount = categoryProductMap.get(category);
            categoryProductMap.put(category, prevCount + 1);
        }
        //have not seen category before in training
        else {
            categoryProductMap.put(category, 1);
        }

        for (Feature f : featuresFromProduct) {
            this.addSeenFeatureInSpecifiedCategory(f, category);
        }
    }

    class ValueComparator implements Comparator<Category> {
        Map<Category, Double> base;

        public ValueComparator(Map<Category, Double> base) {
            this.base = base;
        }

        // Note: this comparator imposes orderings that are inconsistent with equals.
        public int compare(Category a, Category b) {
            if (base.get(a) >= base.get(b)) {
                return -1;
            } else {
                return 1;
            } // returning 0 would merge keys
        }
    }

    /**
     * Generate a list of top three mappings for a product given the destination taxonomy.
     * Use Laplace Smoothing to take into account any categories or features not seen
     * so that the product never results in a value of 0.
     *
     * @param product  Product we are mapping into the destination taxonomy
     * @return mapping of the product into the destination taxonomy
     */
    public List<Mapping> classifyWithBagOfWords(Product product) {
        // Treemap sorts in increasing order of value
        HashMap<Category, Double> map = new HashMap<>();
        ValueComparator bvc = new ValueComparator(map);
        NavigableMap<Category, Double> sorted_map = new TreeMap<>(bvc);

        List<Feature> features = FeatureConverter1.changeProductToFeature(product);
        int totalProducts = NaiveBayesDb.getProductCount(getTaxonomy());

        for (Category category : allDestinationCategories) {
            // P(f_i | C)
            double pProductGivenC = 1.0;
            // P(C)
            double pC = 1.0;

            //category has been seen by classifier during training
            if (categoryFeatureMap.containsKey(category)) {
                Map<Feature, Integer> featureOccurrencesInCategory =
                        NaiveBayesDb.getCategoryFeatureObservationMap(
                                getTaxonomy(), category);

                for (Feature f : features) {
                    //assume f has NOT been seen in this category
                    int count = 0;
                    //update if actually has been seen in this category
                    if (featureOccurrencesInCategory.containsKey(f)) {
                        count = featureOccurrencesInCategory.get(f);
                    }
                    //Laplace smoothing
                    int totalFeaturesInC = categoryFeatureMap.get(category);
                    double pFeatureGivenC = ((double) (1 + count)) /
                            ((double) (totalFeaturesInC + featureSet.size()));
                    pProductGivenC *= pFeatureGivenC;
                }

                int productsInCategory = categoryProductMap.get(category);
                //Laplace smoothing
                pC *= ((double) (productsInCategory + 1)) /
                      ((double) (totalProducts + allDestinationCategories.size()));
            }

            double pCGivenF = pProductGivenC * pC;
            map.put(category, pCGivenF);
        }
        sorted_map.putAll(map);

        List<Mapping> topThreeResults = new ArrayList<>();

        Category firstCategory = sorted_map.pollFirstEntry().getKey();
        Category secondCategory = sorted_map.pollFirstEntry().getKey();
        Category thirdCategory = sorted_map.pollFirstEntry().getKey();

        Mapping m1 = new MappingBuilder().setCategory(firstCategory)
                .setProduct(product).setMethod(Method.CLASSIFIED).createMapping();

        Mapping m2 = new MappingBuilder().setCategory(secondCategory)
                .setProduct(product).setMethod(Method.CLASSIFIED).createMapping();

        Mapping m3 = new MappingBuilder().setCategory(thirdCategory)
                .setProduct(product).setMethod(Method.CLASSIFIED).createMapping();

        topThreeResults.add(m1);
        topThreeResults.add(m2);
        topThreeResults.add(m3);

        return topThreeResults;
    }

    @Override
    public List<Mapping> classify(Product product) {
        return classifyWithBagOfWords(product);
    }

    @Override
    public boolean train(Mapping mapping) {
        trainWithBagOfWordsSingleProduct(
                mapping.getProduct(),mapping.getCategory());
        return true;
    }
}

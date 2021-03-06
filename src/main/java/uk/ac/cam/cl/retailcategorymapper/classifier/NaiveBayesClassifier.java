package uk.ac.cam.cl.retailcategorymapper.classifier;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import uk.ac.cam.cl.retailcategorymapper.classifier.features.NGramFeatureExtractor;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

/**
 * Old Naive Bayes classifier implementation which does not integrate with
 * the database.
 */
public class NaiveBayesClassifier {
    private Set<Feature> featureSet;
    private Set<Category> categorySet;

    //count is across all categories
    private Map<Feature, Integer> totalFeatureCounts;
    // how many times has classifier encountered a
    // category
    private Map<Category, Integer> categoryCounts;
    private Map<Category, Map<Feature, Integer>> featureCountPerCategory;

    public NaiveBayesClassifier() {
        this.featureSet = new HashSet<>();
        this.categorySet = new HashSet<>();
        this.totalFeatureCounts = new HashMap<>();
        this.categoryCounts = new HashMap<>();
        this.featureCountPerCategory = new HashMap<>();
    }

    /**
     * Getter method for set of features seen by classifier in training.
     *
     * @return set of features seen by classifier in training
     */
    public Set<Feature> getFeatures() {
        return this.featureSet;
    }

    /**
     * Getter method for set of categories seen by classifier in training.
     *
     * @return set of categories seen by classifier in training
     */
    public Set<Category> getCategories() {
        return this.categorySet;
    }

    /**
     * Getter method for map of features to features counts as seen in training.
     *
     * @return map of features to features counts seen in training
     */
    public Map<Feature, Integer> getTotalFeatureCounts() {
        return this.totalFeatureCounts;
    }

    /**
     * Getter method for map of categories to count of number of times a category was seen in
     * training.
     *
     * @return map of categories to number of times a category was seen in training
     */
    public Map<Category, Integer> getCategoryCounts() {
        return this.categoryCounts;
    }

    /**
     * Getter method for the Map of Category -> (Feature -> Integer).
     *
     * @return map associating categories to map of features counts
     */
    public Map<Category, Map<Feature, Integer>> getFeatureCountPerCategory() {
        return this.featureCountPerCategory;
    }


    /**
     * Return the number of times a features is seen in a category.
     *
     * @param feature  Feature we want to find the number of occurrences for
     * @param category Category containing products that potentially produced the features that
     *                 we are
     *                 interested in
     * @return count of how many times a features occurs in a given category from potentially
     * multiple products inside the category
     */
    public int getFeatureCountInCategory(Feature feature, Category category) {
        if (this.featureCountPerCategory.containsKey(category)) {
            Map<Feature, Integer> featureCounts = this.featureCountPerCategory.get(category);
            //features seen associated with this category
            if (featureCounts.containsKey(feature)) {
                return featureCounts.get(feature);
            }
            //features NOT seen associated with this category
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
     * Calculate the probability that a features is seen given the category. This represents
     * P(F|C) in the Bayes equation.
     *
     * @param feature  Feature to calculate the probability of
     * @param category Category given
     * @return probability that features is seen given the category
     */
    public double getFeatureProbabilityGivenCategory(Feature feature, Category category) {
        //have never seen this category
        if (!this.categorySet.contains(category)) {
            return 0;
        }
        //have seen this category: (# times seen features in category)/
        // (# times this category has come up = #total features in category)
        else {
            return ((double) this.getFeatureCountInCategory(feature, category)) /
                    ((double) this.categoryCounts.get(category));
        }
    }

    /**
     * Return the total number of times the classifier has seen any category. This number will only
     * include the categories that the classifier has seen through a product/features from within
     * the
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
        } else {
            int timesSeen = this.categoryCounts.get(category);
            //allow for a tiny chance
            if (timesSeen == 0) {
                timesSeen = 1;
            }
            int totalTimesAnyCategorySeen = this.getTotalCategoriesSeen();
            return ((double) timesSeen) / ((double) totalTimesAnyCategorySeen);
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
        } else {
            return 0;
        }
    }

    /**
     * When we go through the training data and see a features linked with a category,
     * call this method to add to the maps and sets of Naive Bayes Classifier.
     *
     * @param featureSeen Feature from product in training set
     * @param category    Destination category that the product which features is derived from is
     *                    put into
     */
    public void addSeenFeatureInSpecifiedCategory(Feature featureSeen, Category category) {
        this.featureSet.add(featureSeen);
        this.categorySet.add(category);

        //update categoryCounts
        //have seen category before
        if (this.categoryCounts.containsKey(category)) {
            int prevCount = this.categoryCounts.get(category);
            this.categoryCounts.put(category, prevCount + 1);
        }
        //have NOT seen category before
        else {
            this.categoryCounts.put(category, 1);
        }

        //update totalFeatureCounts
        //have seen this features before
        if (this.totalFeatureCounts.containsKey(featureSeen)) {
            int prevCount = this.totalFeatureCounts.get(featureSeen);
            this.totalFeatureCounts.put(featureSeen, prevCount + 1); //update count
        }
        // have NOT seen this features before
        else {
            this.totalFeatureCounts.put(featureSeen, 1);
        }

        //update Category -> (Feature -> Integer) Map
        //have previously seen this category associated with a features
        if (this.featureCountPerCategory.containsKey(category)) {
            Map<Feature, Integer> fCountInSpecifiedCategory = this.featureCountPerCategory.get
                    (category);
            //update count of times features is seen in given category (inner map)
            //have seen features before in this category
            if (fCountInSpecifiedCategory.containsKey(featureSeen)) {
                int prevCount = fCountInSpecifiedCategory.get(featureSeen);
                fCountInSpecifiedCategory.put(featureSeen, prevCount + 1);
            }
            //have NOT seen this features before in this category
            else {
                fCountInSpecifiedCategory.put(featureSeen, 1);
            }
            //update outer layer of map
            this.featureCountPerCategory.put(category, fCountInSpecifiedCategory);
        }
        //have NOT see this category associated with a features before
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


    /**
     * Update the sets and maps held by the classifier which will be used for training with
     * information from a product.
     *
     * @param product  Product that has been classified
     * @param category Destination category that product has been mapped to
     */
    public void trainWithBagOfWordsSingleProduct(Product product, Category category) {
        List<Feature> featuresFromProduct = NGramFeatureExtractor.changeProductToFeature(product);

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
     * @param product  Product we are mapping into the destination taxonomy
     * @return mapping of the product into the destination taxonomy
     */
    public Mapping classifyWithBagOfWords(Taxonomy taxonomy, Product product) {
        //treemap sorts in increasing order
        //NavigableMap<Double, Category> probabilityToAllPossibleCategories = new TreeMap<Double,
          //      Category>();
        List<Pair<Double, Category>> outputProbabilities = new ArrayList<>();

        List<Feature> features = NGramFeatureExtractor.changeProductToFeature(product);
        Set<Category> allDestinationCategories = taxonomy.getCategories();

        //take a single category
        for (Category category : allDestinationCategories) {

            //calculate P(f_i | C)
            double pProductGivenC = 1.0;
            //category has been seen by classifier during training
            if (this.categorySet.contains(category)) {
                Map<Feature, Integer> featureOccurrencesInCategory = this.featureCountPerCategory
                        .get(category);
                for (Feature f : features) {
                    if (this.totalFeatureCounts.containsKey(f)) {
                        // Skip features which have only been seen once in training
                        if (this.totalFeatureCounts.get(f) < 2) continue;
                        //assume f has NOT been seen in this category
                        int count = 0;
                        //update if it has been seen in this category
                        if (featureOccurrencesInCategory.containsKey(f)) {
                            count = featureOccurrencesInCategory.get(f);
                        }
                        //Laplace smoothing
                        double pFeatureGivenC = ((double) (1 + count)) / ((double)
                                (featureOccurrencesInCategory.size() + this.featureSet.size()));

                        pProductGivenC *= pFeatureGivenC;
                    } else {
                        // Skip features which have never been seen in training
                        System.out.format("skipping feature %s, not seen in training\n", f
                                .toString());
                    }
                }
            }
            //category has not been seen by the classifier during training ?!?!
            else {
                continue;
                //System.err.println("category has not been seen by the classifier during
                // training (1)");

            }
            //calculate P(C)
            double pC = 1.0;
            //category seen by classifier during training
            if (this.categorySet.contains(category)) {
                int catCount = this.getCategoryCount(category);
                int totalSeenCategoryCount = this.getTotalCategoriesSeen();
                int numberAllCategories = allDestinationCategories.size();
                //Laplace smoothing
                pC *= ((double) (catCount + 1)) / ((double) (totalSeenCategoryCount +
                        numberAllCategories));
            }
            //category NOT seen by classifier during training
            else {

                
            }
            double pCGivenF = pProductGivenC * pC;
            //probabilityToAllPossibleCategories.put(pCGivenF, category);
            outputProbabilities.add(new ImmutablePair<>(pCGivenF, category));
        }

        //Category mostLikelyCategory = probabilityToAllPossibleCategories.lastEntry().getValue();
        Category mostLikelyCategory = Collections.max(outputProbabilities, (Pair<Double,
                Category> one, Pair<Double, Category> two) -> Double.compare(one.getLeft(), two
                .getLeft())).getRight();
        Mapping m = (new MappingBuilder()).setCategory(mostLikelyCategory)
                .setProduct(product).setMethod(Method.CLASSIFIED).createMapping();
        return m;
    }


    public void trainWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
                                 double nameWeight, double descriptionWeight, double priceWeight,
                                 double destinationCategoryWeight) {
        throw new UnsupportedOperationException();
    }

    public Mapping classifyWithWeights(Taxonomy taxonomy, Product product, double
            originalCategoryWeight, double nameWeight, double descriptionWeight, double
            priceWeight) {
        throw new UnsupportedOperationException();
    }

    public static void main(String[] args) throws IOException {
        XmlMappingUnmarshaller unmarshaller = new XmlMappingUnmarshaller();
        List<Mapping> inputMappings = unmarshaller.unmarshal(new String(Files.readAllBytes(Paths
                .get(args[0])), StandardCharsets.UTF_8));
        List<Mapping> trainMappings = new ArrayList<>();
        List<Mapping> testMappings = new ArrayList<>();
        Random rand = new Random();
        for (Mapping m : inputMappings) {
            // Split products 80/20 into train/test
            if (rand.nextDouble() > 0.8) {
                testMappings.add(m);
            } else {
                trainMappings.add(m);
            }
        }
        NaiveBayesClassifier classifier = new NaiveBayesClassifier();
        TaxonomyBuilder taxonomyBuilder = new TaxonomyBuilder();
        taxonomyBuilder.setName("Test Taxonomy");
        Taxonomy t = taxonomyBuilder.createNonDbTaxonomy();
        for (Mapping m : trainMappings) {
            //CategoryBuilder b = new CategoryBuilder();
            //b.setParts(new String[] { p.getDestinationCategory().getPart(0) });
            //Category topLevelOnly = b.createCategory();
            classifier.trainWithBagOfWordsSingleProduct(m.getProduct(), m.getCategory());
            t.getCategories().add(m.getCategory());
        }
        //List<Product> testProducts = unmarshaller.unmarshal(new String(Files.readAllBytes(Paths
          //      .get(args[1])), StandardCharsets.UTF_8));
        for (Mapping testMapping : testMappings) {
            Product p = testMapping.getProduct();
            Mapping classifiedMapping = classifier.classifyWithBagOfWords(t, p);
            System.out.format("Classified as %s: %s (originally, %s)\n", classifiedMapping
                            .getCategory().toString(), p.getName(), testMapping.getCategory()
                    .toString());
        }
    }
}

package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.classifier.features.FeatureConverter1;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.FeatureSource;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A Naive Bayes classifier implementation which integrates with the database.
 */
public class NaiveBayesDbClassifier extends Classifier {
    private Set<Feature> taxonomyFeatureSet;
    private Map<Category, Integer> categoryProductCount;
    private Map<Category, Integer> categoryFeatureCount;
    private Set<Category> destinationCategories;
    private Map<Category, Map<Feature, Integer>> categoryFeatureObservationMaps;
    private int totalProducts;
    private int destinationCategoriesSize;

    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public NaiveBayesDbClassifier(Taxonomy taxonomy) {
        super(taxonomy);
        taxonomyFeatureSet = new HashSet<>(
                NaiveBayesDb.getFeatureSet(taxonomy));
        categoryProductCount = new HashMap<>(
                NaiveBayesDb.getCategoryProductMap(taxonomy));
        categoryFeatureCount = new HashMap<>(
                NaiveBayesDb.getCategoryFeatureMap(taxonomy));
        destinationCategories = new HashSet<>(taxonomy.getCategories());

        categoryFeatureObservationMaps = new HashMap<>();
        for (Category category : destinationCategories) {
            categoryFeatureObservationMaps.put(category,
                    new HashMap<>(NaiveBayesDb.getCategoryFeatureObservationMap(
                            getTaxonomy(), category)));
        }

        totalProducts = NaiveBayesDb.getProductCount(getTaxonomy());
        destinationCategoriesSize = destinationCategories.size();
    }

    /**
     * Generate a list of top three mappings for a product given the destination taxonomy.
     * Use Laplace Smoothing to take into account any categories or features not seen
     * so that a product never results in a value of 0.
     *
     * @param product Product we are mapping into the destination taxonomy
     * @return Mapping of the product into the destination taxonomy
     */
    @Override
    public List<Mapping> classify(Product product) {
        List<Feature> features = FeatureConverter1.changeProductToFeature(product);

        TreeMap<Double, MappingBuilder> matches = new TreeMap<>();

        for (Category category : destinationCategories) {
            // P(f_i | C)
            double pProductGivenC = 0.0;
            // P(C)
            double pC = 0.0;

            //category has been seen by classifier during training
            if (categoryFeatureCount.containsKey(category)) {
                int productsInCategory = categoryProductCount.get(category);
                int totalFeaturesInC = categoryFeatureCount.get(category);

                Map<Feature, Integer> featureOccurrencesInCategory =
                        categoryFeatureObservationMaps.get(category);

                for (Feature f : features) {
                    //assume f has NOT been seen in this category
                    int count = 0;
                    //update if actually has been seen in this category
                    if (featureOccurrencesInCategory.containsKey(f)) {
                        count = featureOccurrencesInCategory.get(f);
                    }
                    //Laplace smoothing
                    double pFeatureGivenC = ((double) (count + 1)) /
                            ((double) (totalFeaturesInC + taxonomyFeatureSet.size()));
                    pProductGivenC += Math.log10(pFeatureGivenC);
                }

                //Laplace smoothing
                pC += Math.log(((double) (productsInCategory + 1)) /
                      ((double) (totalProducts + destinationCategoriesSize)));
            }

            double pCGivenF = pProductGivenC + pC;

            matches.put(pCGivenF,
                    new MappingBuilder().setProduct(product)
                            .setTaxonomy(getTaxonomy())
                            .setCategory(category)
                            .setMethod(Method.CLASSIFIED));
        }

        List<Entry<Double, MappingBuilder>> topThree = new ArrayList<>();
        double topThreeSum = 0;
        for (int i = 0; i < 3; i++) {
            if (matches.size() == 0) {
                break;
            }
            Entry<Double, MappingBuilder> mappingBuilderEntry = matches.lastEntry();
            topThree.add(mappingBuilderEntry);
            topThreeSum += mappingBuilderEntry.getKey();
        }

        List<Mapping> result = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Entry<Double, MappingBuilder> mbe = topThree.get(i);
            MappingBuilder mb = mbe.getValue();
            double confidence = mbe.getKey()/topThreeSum;
            mb.setConfidence(confidence);
            result.add(mb.createMapping());
        }
        return result;
    }

    public List<Mapping> classifyWithWeights(Product product, double nameWeight,
                                             double descriptionWeight, double priceWeight,
                                             double originalCategoryWeight, double attributeWeight) {

        if (nameWeight+descriptionWeight+priceWeight+originalCategoryWeight+attributeWeight!=1.0) {
            throw new RuntimeException("the weights don't sum to 1");
        }


        List<Feature> features = FeatureConverter1.changeProductToFeature(product);

        TreeMap<Double, MappingBuilder> matches = new TreeMap<>();

        for (Category category : destinationCategories) {
            // P(f_i | C)
            double pProductGivenC = 0.0;
            // P(C)
            double pC = 0.0;

            //category has been seen by classifier during training
            if (categoryFeatureCount.containsKey(category)) {
                int productsInCategory = categoryProductCount.get(category);
                int totalFeaturesInC = categoryFeatureCount.get(category);

                Map<Feature, Integer> featureOccurrencesInCategory =
                        categoryFeatureObservationMaps.get(category);

                for (Feature f : features) {
                    double correctWeight;
                    if (f.getSource() == FeatureSource.NAME) {
                        correctWeight = nameWeight;
                    } else if (f.getSource() == FeatureSource.DESCRIPTION) {
                        correctWeight = descriptionWeight;
                    } else if (f.getSource() == FeatureSource.PRICE) {
                        correctWeight = priceWeight;
                    } else if (f.getSource() == FeatureSource.ORIGINAL_CATEGORY) {
                        correctWeight = originalCategoryWeight;
                    } else {
                        correctWeight = attributeWeight;
                    }

                    //assume f has NOT been seen in this category
                    int count = 0;
                    //update if actually has been seen in this category
                    if (featureOccurrencesInCategory.containsKey(f)) {
                        count = featureOccurrencesInCategory.get(f);
                    }
                    //Laplace smoothing
                    double pFeatureGivenC = (((double) (count + 1)) * correctWeight) /
                            ((double) (totalFeaturesInC + taxonomyFeatureSet.size()));
                    pProductGivenC += Math.log10(pFeatureGivenC);
                }

                //Laplace smoothing
                pC += Math.log(((double) (productsInCategory + 1)) /
                        ((double) (totalProducts + destinationCategoriesSize)));
            }

            double pCGivenF = pProductGivenC + pC;


            matches.put(pCGivenF,
                    new MappingBuilder().setProduct(product)
                            .setTaxonomy(getTaxonomy())
                            .setCategory(category)
                            .setMethod(Method.CLASSIFIED));
        }

        List<Entry<Double, MappingBuilder>> topThree = new ArrayList<>();
        double topThreeSum = 0;
        for (int i = 0; i < 3; i++) {
            if (matches.size() == 0) {
                break;
            }
            Entry<Double, MappingBuilder> mappingBuilderEntry = matches.lastEntry();
            topThree.add(mappingBuilderEntry);
            topThreeSum += mappingBuilderEntry.getKey();
        }

        List<Mapping> result = new ArrayList<>();
        for (int i=0; i<3; i++) {
            Entry<Double, MappingBuilder> mbe = topThree.get(i);
            MappingBuilder mb = mbe.getValue();
            double confidence = mbe.getKey()/topThreeSum;
            mb.setConfidence(confidence);
            result.add(mb.createMapping());
        }
        return result;
    }


}

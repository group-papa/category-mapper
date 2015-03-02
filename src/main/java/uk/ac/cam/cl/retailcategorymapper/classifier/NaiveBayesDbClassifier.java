package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.classifier.features.NGramFeatureExtractor;
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
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
    private NaiveBayesStorage storage;

    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public NaiveBayesDbClassifier(Taxonomy taxonomy, NaiveBayesStorage storage) {
        super(taxonomy);
        this.storage = storage;
        taxonomyFeatureSet = new HashSet<>(
                storage.getFeatureSet(taxonomy));
        categoryProductCount = new HashMap<>(
                storage.getCategoryProductMap(taxonomy));
        categoryFeatureCount = new HashMap<>(
                storage.getCategoryFeatureMap(taxonomy));
        destinationCategories = new HashSet<>(taxonomy.getCategories());

        categoryFeatureObservationMaps = new HashMap<>();
        for (Category category : destinationCategories) {
            categoryFeatureObservationMaps.put(category,
                    new HashMap<>(storage.getCategoryFeatureObservationMap(
                            getTaxonomy(), category)));
        }

        totalProducts = storage.getProductCount(getTaxonomy());
        destinationCategoriesSize = destinationCategories.size();
    }

    public NaiveBayesDbClassifier(Taxonomy taxonomy) {
        this(taxonomy, NaiveBayesDb.getInstance());
    }

    class DoubleMBTuple {
        private final Double x;
        private final MappingBuilder y;

        public DoubleMBTuple(Double x, MappingBuilder y) {
            this.x = x;
            this.y = y;
        }

        public Double getDouble() {
            return this.x;
        }

        public MappingBuilder getMappingBuilder() {
            return this.y;
        }
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
        //List<Feature> features = FeatureConverter.changeProductToFeature(product);
        List<Feature> features = NGramFeatureExtractor.changeProductToFeature(product);
        TreeMap<Double, Set<MappingBuilder>> matches = new TreeMap<>();

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

                    if(f.getSource()==FeatureSource.DESCRIPTION){
                        pFeatureGivenC+=0.01;
                    }
                    if(f.getSource()==FeatureSource.NAME){
                        pFeatureGivenC+=0.005;
                    }

                    pProductGivenC += Math.log10(pFeatureGivenC);

                }

                //Laplace smoothing
                pC += Math.log10(((double) (productsInCategory + 1)) /
                        ((double) (totalProducts + destinationCategoriesSize)));
            }

            //category has not been seen by classifier in training
            else {
                if (taxonomyFeatureSet.size() > 0) {
                    pProductGivenC += features.size()
                            * Math.log10(((double) 1)
                            /  ((double) taxonomyFeatureSet.size()));
                }
                if (destinationCategoriesSize > 0) {
                    pC += Math.log10(((double) (1)) /
                            ((double) (destinationCategoriesSize)));
                }
            }
            double pCGivenF = pProductGivenC + pC;

            Set<MappingBuilder> prevValue = matches.get(pCGivenF);
            if (prevValue == null) {
                prevValue = new HashSet<>();
                prevValue.add(new MappingBuilder()
                        .setProduct(product)
                        .setTaxonomy(getTaxonomy())
                        .setCategory(category)
                        .setMethod(Method.CLASSIFIED));
            } else {
                prevValue.add(new MappingBuilder()
                        .setProduct(product)
                        .setTaxonomy(getTaxonomy())
                        .setCategory(category)
                        .setMethod(Method.CLASSIFIED));
            }
            matches.put(pCGivenF, prevValue);
        }

        List<DoubleMBTuple> topThree = new ArrayList<>();
        double topThreeProbSum = 0;
        int countToThree = 0;
        while (countToThree < 3) {
            if (matches.size() == 0) {
                break;
            }
            Entry<Double, Set<MappingBuilder>> mappingBuilderSetEntry = matches.pollLastEntry();
            Double d = mappingBuilderSetEntry.getKey();
            Set<MappingBuilder> mbSet = mappingBuilderSetEntry.getValue();
            int setSize = mbSet.size();
            //can safely add all
            if ((countToThree + setSize) < 3) {
                for (MappingBuilder mb : mbSet) {
                    DoubleMBTuple probMBTuple = new DoubleMBTuple(d, mb);
                    topThree.add(probMBTuple);
                    topThreeProbSum += d;
                }
                countToThree += setSize;
            }
            //too many to add all
            else {
                int nAdded = 0;
                Iterator iter = mbSet.iterator();
                while ((countToThree + nAdded) < 3) {
                    MappingBuilder mb = (MappingBuilder) iter.next();
                    DoubleMBTuple probMBTuple = new DoubleMBTuple(d, mb);
                    topThree.add(probMBTuple);
                    topThreeProbSum += d;
                    nAdded++;
                }
                countToThree += nAdded;
            }
        }

        List<Mapping> result = new ArrayList<>();
        int maximum = Math.min(3, topThree.size());
        for (int i = 0; i < maximum; i++) {
            DoubleMBTuple mbTuple = topThree.get(i);
            MappingBuilder mb = mbTuple.getMappingBuilder();
            double confidence = mbTuple.getDouble() / topThreeProbSum;
            if (Double.isNaN(confidence)) {
                confidence = mbTuple.getDouble();
            }
            mb.setConfidence(confidence);
            result.add(mb.createMapping());
        }

        result.sort(new Comparator<Mapping>() {
            @Override
            public int compare(Mapping o1, Mapping o2) {
                return Double.compare(-o1.getConfidence(), -o2.getConfidence());
            }
        });

        return result;
    }

    public List<Mapping> classifyWithWeights(Product product, double nameWeight,
                                             double originalCategoryWeight) {
        if (nameWeight + originalCategoryWeight != 1.0) {
            throw new RuntimeException("the weights don't sum to 1");
        }

        //List<Feature> features = FeatureConverter.changeProductToFeature(product);
        List<Feature> features = NGramFeatureExtractor.changeProductToFeature(product);
        TreeMap<Double, Set<MappingBuilder>> matches = new TreeMap<>();

        Set<Integer> nFeats = new HashSet<>();
        for (Category category : destinationCategories) {
            // P(f_i | C)
            double pProductGivenC = 0.0;
            // P(C)
            double pC = 0.0;

            //category has been seen by classifier during training
            if (categoryFeatureCount.containsKey(category)) {
                int productsInCategory = categoryProductCount.get(category);
                int totalFeaturesInC = categoryFeatureCount.get(category);
                nFeats.add(totalFeaturesInC);

                Map<Feature, Integer> featureOccurrencesInCategory =
                        categoryFeatureObservationMaps.get(category);

                for (Feature f : features) {
                    double correctWeight = 1.0;
                    if (f.getSource().equals(FeatureSource.NAME)) {
                        correctWeight *= nameWeight;
                    } else if (f.getSource().equals(FeatureSource.ORIGINAL_CATEGORY)) {
                        correctWeight *= originalCategoryWeight;
                    } else {
                        throw new RuntimeException("source of feature doesn't " +
                                "match expectation");
                    }

                    //assume f has NOT been seen in this category
                    int count = 0;
                    //update if actually has been seen in this category
                    if (featureOccurrencesInCategory.containsKey(f)) {
                        count = featureOccurrencesInCategory.get(f);
                    }
                    //Laplace smoothing
                    double pFeatureGivenC = correctWeight * ((double) (count + 1)) /
                            ((double) (totalFeaturesInC + taxonomyFeatureSet.size()));
                    pProductGivenC += Math.log10(pFeatureGivenC);
                }

                //Laplace smoothing
                pC += Math.log10(((double) (productsInCategory + 1)) /
                        ((double) (totalProducts + destinationCategoriesSize)));
            }

            //category has not been seen by classifier in training
            //no weights used in this section
            else {
                if (taxonomyFeatureSet.size() > 0) {
                    pProductGivenC += features.size()
                            * Math.log10(((double) 1)
                            /  ((double) taxonomyFeatureSet.size()));
                }
                if (destinationCategoriesSize > 0) {
                    pC += Math.log10(((double) (1)) /
                            ((double) (destinationCategoriesSize)));
                }
            }
            double pCGivenF = pProductGivenC + pC;

            Set<MappingBuilder> prevValue = matches.get(pCGivenF);
            if (prevValue == null) {
                prevValue = new HashSet<>();
                prevValue.add(new MappingBuilder()
                        .setProduct(product)
                        .setTaxonomy(getTaxonomy())
                        .setCategory(category)
                        .setMethod(Method.CLASSIFIED));
            } else {
                prevValue.add(new MappingBuilder()
                        .setProduct(product)
                        .setTaxonomy(getTaxonomy())
                        .setCategory(category)
                        .setMethod(Method.CLASSIFIED));
            }
            matches.put(pCGivenF, prevValue);
        }


        List<DoubleMBTuple> topThree = new ArrayList<>();
        double topThreeProbSum = 0;
        int countToThree = 0;
        while (countToThree < 3) {
            if (matches.size() == 0) {
                break;
            }
            Entry<Double, Set<MappingBuilder>> mappingBuilderSetEntry = matches.pollLastEntry();
            Double d = mappingBuilderSetEntry.getKey();
            Set<MappingBuilder> mbSet = mappingBuilderSetEntry.getValue();
            int setSize = mbSet.size();
            //can safely add all
            if ((countToThree + setSize) < 3) {
                for (MappingBuilder mb : mbSet) {
                    DoubleMBTuple probMBTuple = new DoubleMBTuple(d, mb);
                    topThree.add(probMBTuple);
                    topThreeProbSum += d;
                }
                countToThree += setSize;
            }
            //too many to add all
            else {
                int nAdded = 0;
                Iterator iter = mbSet.iterator();
                while ((countToThree + nAdded) < 3) {
                    MappingBuilder mb = (MappingBuilder) iter.next();
                    DoubleMBTuple probMBTuple = new DoubleMBTuple(d, mb);
                    topThree.add(probMBTuple);
                    topThreeProbSum += d;
                    nAdded++;
                }
                countToThree += nAdded;
            }
        }

        List<Mapping> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            DoubleMBTuple mbTuple = topThree.get(i);
            MappingBuilder mb = mbTuple.getMappingBuilder();
            double confidence = mbTuple.getDouble();
            if (topThreeProbSum != 0.0) {
                confidence /= topThreeProbSum;
            }
            mb.setConfidence(confidence);
            result.add(mb.createMapping());
        }

        result.sort(new Comparator<Mapping>() {
            @Override
            public int compare(Mapping o1, Mapping o2) {
                return Double.compare(-o1.getConfidence(), -o2.getConfidence());
            }
        });

        return result;
    }
}


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

    private Map<C, Map<F, Integer>> featureCountPerCategory;
    private Map<F, Integer> numberOfFeatureOccurrences;

    public NaiveBayesClassifier() {
        this.featureCountPerCategory = new HashMap<C, Map<F, Integer>>();
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

    @Override
    public Mapping classify(Taxonomy taxonomy, Product product) {
        throw new UnsupportedOperationException();
    }
}

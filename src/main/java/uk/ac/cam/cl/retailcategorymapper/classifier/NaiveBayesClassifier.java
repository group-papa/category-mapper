package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

/**
 * A Naive Bayes classifier implementation.
 */
public class NaiveBayesClassifier implements Classifier {
    @Override
    public Mapping classify(Taxonomy taxonomy, Product product) {
        throw new UnsupportedOperationException();
    }
}

package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;

/**
 * An abstract class for classifiers.
 */
public abstract class Classifier {
    /**
     * The taxonomy this classifier is for.
     */
    private Taxonomy taxonomy;

    /**
     * Construct a new classifier for a given taxonomy.
     * @param taxonomy The taxonomy.
     */
    public Classifier(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    /**
     * Get the taxonomy this classifier is for.
     * @return The taxonomy.
     */
    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    /**
     * Classify the given product into the given taxonomy.
     * @param product The product.
     * @return A list of possible mappings.
     */
    public abstract List<Mapping> classify(Product product);
}

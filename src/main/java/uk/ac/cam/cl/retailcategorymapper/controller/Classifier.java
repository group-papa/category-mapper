package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;

/**
 * An interface for all classifiers used by the category mapper.
 */
public interface Classifier {
    /**
     * Classify the given product into the given taxonomy.
     * @param taxonomy The taxonomy to classify the product into.
     * @param product The product.
     * @return A list of possible mappings.
     */
    public List<Mapping> classify(Taxonomy taxonomy, Product product);

    /**
     * Use the given mapping to train the classifer.
     * @param mapping The mapping to train on.
     * @return Whether the mapping was used to train the classifer.
     */
    public boolean train(Mapping mapping);
}

package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;

/**
 * An interface for all classifiers used by the category mapper.
 */
public interface Classifier {
    public List<Mapping> classify(Taxonomy taxonomy, Product product);
    public void train(Mapping mapping);
}

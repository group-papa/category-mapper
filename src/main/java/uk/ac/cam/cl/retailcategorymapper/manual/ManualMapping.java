package uk.ac.cam.cl.retailcategorymapper.manual;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.List;

/**
 * A classifier implementation for manual mappings.
 */
public class ManualMapping implements Classifier {
    @Override
    public List<Mapping> classify(Taxonomy taxonomy, Product product) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void train(Mapping mapping) {
        throw new UnsupportedOperationException();
    }
}

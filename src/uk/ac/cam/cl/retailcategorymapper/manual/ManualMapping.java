package uk.ac.cam.cl.retailcategorymapper.manual;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

public class ManualMapping implements Classifier {
    @Override
    public Mapping classify(Taxonomy taxonomy, Product product) {
        throw new UnsupportedOperationException();
    }
}

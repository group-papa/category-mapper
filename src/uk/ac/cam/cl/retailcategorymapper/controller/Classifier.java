package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

public interface Classifier {
    public Mapping classify(Taxonomy taxonomy, Product product);
}

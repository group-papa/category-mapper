package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An class to store a response after requesting classifications.
 */
public class ClassifyResponse extends Response {
    private List<Mapping> products;

    public ClassifyResponse(Taxonomy taxonomy, List<Mapping> products) {
        super(taxonomy);
        this.products = new ArrayList<>(products);
    }

    public List<Mapping> getProducts() {
        return Collections.unmodifiableList(this.products);
    }
}

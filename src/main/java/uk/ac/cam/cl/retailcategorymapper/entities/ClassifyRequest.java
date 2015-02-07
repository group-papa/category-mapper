package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An class to store a request to classify products.
 */
public class ClassifyRequest extends Request {
    private List<Product> products;

    public ClassifyRequest(Taxonomy taxonomy, List<Product> products) {
        super(taxonomy);
        this.products = new ArrayList<>(products);
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(this.products);
    }
}

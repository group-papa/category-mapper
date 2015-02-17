package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An class to store a response after requesting classifications.
 */
public class ClassifyResponse extends Response {
    private final Map<Product, List<Mapping>> mappings;

    public ClassifyResponse(Taxonomy taxonomy, Map<Product,
            List<Mapping>> mappings) {
        super(taxonomy);
        this.mappings = new HashMap<>(mappings);
    }

    public Map<Product, List<Mapping>> getMappings() {
        return Collections.unmodifiableMap(this.mappings);
    }
}

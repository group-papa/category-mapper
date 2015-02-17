package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An class to store a request to train the system.
 */
public class TrainRequest extends Request {
    private final List<Mapping> mappings;
    private final boolean addToManualMappings;
    private final boolean addToTrainingSet;

    public TrainRequest(Taxonomy taxonomy, List<Mapping> mappings,
                        boolean addToManualMappings, boolean addToTrainingSet) {
        super(taxonomy);
        this.mappings = new ArrayList<>(mappings);
        this.addToManualMappings = addToManualMappings;
        this.addToTrainingSet = addToTrainingSet;
    }

    public List<Mapping> getMappings() {
        return Collections.unmodifiableList(this.mappings);
    }

    public boolean getAddToManualMappings() {
        return this.addToManualMappings;
    }

    public boolean getAddToTrainingSet() {
        return this.addToTrainingSet;
    }
}

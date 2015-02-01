package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An class to store a request to train the system.
 */
public class TrainRequest extends Request {
    private boolean addToManualMappings;
    private boolean addToTrainingSet;
    private List<Mapping> mappings;

    public TrainRequest(Taxonomy taxonomy, boolean addToManualMappings,
                        boolean addToTrainingSet, List<Mapping> mappings) {
        super(taxonomy);
        this.addToManualMappings = addToManualMappings;
        this.addToTrainingSet = addToTrainingSet;
        this.mappings = new ArrayList<>(mappings);
    }

    public boolean getAddToManualMappings() {
        return this.addToManualMappings;
    }

    public boolean getAddToTrainingSet() {
        return this.addToTrainingSet;
    }

    public List<Mapping> getMappings() {
        return Collections.unmodifiableList(this.mappings);
    }
}

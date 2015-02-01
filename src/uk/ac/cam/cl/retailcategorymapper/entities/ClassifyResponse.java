package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.List;

public class ClassifyResponse implements Response {
    private List<Mapping> mappingsMade;

    public ClassifyResponse(List<Mapping> mappingsMade) {
        this.mappingsMade = mappingsMade;
    }

    public List<Mapping> getMappingsMade() {
        return this.mappingsMade();
    }
}

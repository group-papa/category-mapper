package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to store a details about a download. Mappings are modifiable so
 * that corrections can be made.
 */
public class Download {
    private String id;
    private Map<String, Mapping> mappings;

    @JsonCreator
    public Download(@JsonProperty("mappings") Map<String, Mapping> mappings) {
        this.id = Uuid.generateUUID();
        this.mappings = new HashMap<>(mappings);
    }

    public String getId() {
        return id;
    }

    public Map<String, Mapping> getMappings() {
        return mappings;
    }
}

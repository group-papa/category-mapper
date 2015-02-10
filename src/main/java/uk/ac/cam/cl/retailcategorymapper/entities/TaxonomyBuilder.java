package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * A builder class which produces an immutable Taxonomy.
 */
@JsonPOJOBuilder(buildMethodName = "createTaxonomy")
public class TaxonomyBuilder {
    private String id;
    private String name;
    private String dateCreated;

    @JsonProperty("id")
    public TaxonomyBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public TaxonomyBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("dateCreated")
    public TaxonomyBuilder setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    public Taxonomy createTaxonomy() {
        return new Taxonomy(id, name, dateCreated);
    }
}
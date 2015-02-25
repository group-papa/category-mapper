package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * A builder class which produces an immutable Category.
 */
@JsonPOJOBuilder(buildMethodName = "createCategory")
public class CategoryBuilder {
    private String id;
    private String[] parts;
    private Taxonomy subTaxonomy = null;

    @JsonProperty("id")
    public CategoryBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("parts")
    public CategoryBuilder setParts(String[] parts) {
        this.parts = parts;
        return this;
    }

    @JsonProperty("subtaxonomy")
    public CategoryBuilder setSubTaxonomy(Taxonomy subTaxonomy) {
        this.subTaxonomy = subTaxonomy;
        return this;
    }

    public Category createCategory() {
        return new Category(id, parts, subTaxonomy);
    }
}

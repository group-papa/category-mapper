package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * A builder class which produces an immutable Mapping.
 */
@JsonPOJOBuilder(buildMethodName = "createMapping")
public class MappingBuilder {
    private Product product;
    private Category category;
    private Taxonomy taxonomy;
    private Method method;
    private double confidence = Double.MIN_VALUE;

    @JsonProperty("product")
    public MappingBuilder setProduct(Product product) {
        this.product = product;
        return this;
    }

    @JsonProperty("category")
    public MappingBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    @JsonProperty("taxonomy")
    public MappingBuilder setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
        return this;
    }

    @JsonProperty("method")
    public MappingBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    @JsonProperty("confidence")
    public MappingBuilder setConfidence(double confidence) {
        this.confidence = confidence;
        return this;
    }

    public Mapping createMapping() {
        return new Mapping(product, category, taxonomy, method, confidence);
    }
}

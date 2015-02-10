package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * A builder class which produces an immutable Mapping.
 */
public class MappingBuilder {
    private Product product;
    private Category category;
    private Taxonomy taxonomy;
    private Method method;
    private float confidence;

    public MappingBuilder setProduct(Product product) {
        this.product = product;
        return this;
    }

    public MappingBuilder setCategory(Category category) {
        this.category = category;
        return this;
    }

    public MappingBuilder setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
        return this;
    }

    public MappingBuilder setMethod(Method method) {
        this.method = method;
        return this;
    }

    public MappingBuilder setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    public Mapping createMapping() {
        return new Mapping(product, category, taxonomy, method, confidence);
    }
}

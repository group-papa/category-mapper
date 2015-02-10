package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * An immutable class to store a details about a mapping.
 */
public class Mapping {
    private Product product;
    private Category category;
    private Taxonomy taxonomy;
    private Method method;
    private float confidence;

    protected Mapping(Product product, Category category, Taxonomy taxonomy,
                      Method method, float confidence) {
        this.product = product;
        this.category = category;
        this.taxonomy = taxonomy;
        this.method = method;
        this.confidence = confidence;
    }

    public Product getProduct() {
        return product;
    }

    public Category getCategory() {
        return category;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public Method getMethod() {
        return method;
    }

    public float getConfidence() {
        return confidence;
    }
}

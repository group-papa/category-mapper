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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mapping)) return false;

        Mapping mapping = (Mapping) o;

        if (Float.compare(mapping.confidence, confidence) != 0) return false;
        if (!category.equals(mapping.category)) return false;
        if (method != mapping.method) return false;
        if (!product.equals(mapping.product)) return false;
        if (taxonomy != null ? !taxonomy.equals(mapping.taxonomy) : mapping.taxonomy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = product.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + (taxonomy != null ? taxonomy.hashCode() : 0);
        result = 31 * result + (method != null ? method.hashCode() : 0);
        result = 31 * result + (confidence != +0.0f ? Float.floatToIntBits(confidence) : 0);
        return result;
    }
}

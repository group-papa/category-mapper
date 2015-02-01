package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * An immutable class to store a details about a mapping.
 */
public class Mapping {
    private Product product;
    private Category category;
    private Method method;
    private float confidence;

    protected Mapping(Product product, Category category, Method method, float
                      confidence) {
        this.product = product;
        this.category = category;
        this.method = method;
        this.confidence = confidence;
    }

    public Product getProduct() {
        return product;
    }

    public Category getCategory() {
        return category;
    }

    public Method getMethod() {
        return method;
    }

    public float getConfidence() {
        return confidence;
    }
}

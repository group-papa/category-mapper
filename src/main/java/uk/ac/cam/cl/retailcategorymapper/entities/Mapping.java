package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Comparator;

/**
 * An immutable class to store a details about a mapping.
 */
@JsonDeserialize(builder = MappingBuilder.class)
public class Mapping {
    private Product product;
    private Category category;
    private Taxonomy taxonomy;
    private Method method;
    private double confidence;

    protected Mapping(Product product, Category category, Taxonomy taxonomy,
                      Method method, double confidence) {
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

    public double getConfidence() {
        return confidence;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mapping)) return false;

        Mapping mapping = (Mapping) o;

        if (Double.compare(mapping.confidence, confidence) != 0) return false;
        if (!category.equals(mapping.category)) return false;
        if (method != mapping.method) return false;
        if (!product.equals(mapping.product)) return false;
        if (taxonomy != null ? !taxonomy.equals(mapping.taxonomy) : mapping.taxonomy != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = product.hashCode();
        result = 31 * result + category.hashCode();
        result = 31 * result + (taxonomy != null ? taxonomy.hashCode() : 0);
        result = 31 * result + method.hashCode();
        temp = Double.doubleToLongBits(confidence);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public static class ConfidenceSorter implements Comparator<Mapping> {
        @Override
        public int compare(Mapping o1, Mapping o2) {
            return Double.compare(o1.getConfidence(), o2.getConfidence());
        }
    }
}

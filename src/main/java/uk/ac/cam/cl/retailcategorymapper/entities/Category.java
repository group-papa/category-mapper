package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.Arrays;

/**
 * An immutable class to store a details about a category.
 */
public class Category {
    private String[] parts;
    private Taxonomy taxonomy;

    protected Category(String[] parts, Taxonomy taxonomy) {
        this.parts = Arrays.copyOf(parts, parts.length);
        this.taxonomy = taxonomy;
    }

    public String getPart(int n) {
        return parts[n];
    }

    public String[] getAll() {
        return Arrays.copyOf(parts, parts.length);
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public String toString() {
        throw new UnsupportedOperationException();
    }
}

package uk.ac.cam.cl.retailcategorymapper.entities;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * An immutable class to store a details about a category.
 */
public class Category {
    private String[] parts;
    private Taxonomy taxonomy;

    protected Category(String[] parts, Taxonomy taxonomy) {
        if (parts.length == 0) {
            throw new IllegalArgumentException();
        }

        this.parts = Arrays.copyOf(parts, parts.length);
        this.taxonomy = taxonomy;
    }

    public String getPart(int n) {
        return parts[n];
    }

    public String[] getAllParts() {
        return Arrays.copyOf(parts, parts.length);
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public String toString() {
        return StringUtils.join(parts, " > ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (!Arrays.equals(parts, category.parts)) return false;
        if (!taxonomy.equals(category.taxonomy)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(parts);
        result = 31 * result + taxonomy.hashCode();
        return result;
    }
}

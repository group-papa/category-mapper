package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

/**
 * An immutable class to store details about a taxonomy.
 */
@JsonDeserialize(builder = TaxonomyBuilder.class)
public class Taxonomy {
    private String id;
    private String name;

    protected Taxonomy(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Taxonomy)) return false;

        Taxonomy taxonomy = (Taxonomy) o;

        if (!id.equals(taxonomy.id)) return false;
        if (!name.equals(taxonomy.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}

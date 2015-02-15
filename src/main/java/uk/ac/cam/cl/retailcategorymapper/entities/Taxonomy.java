package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;

import java.util.List;

/**
 * An immutable class to store details about a taxonomy.
 */
@JsonDeserialize(builder = TaxonomyBuilder.class)
public class Taxonomy {
    private String id;
    private String name;
    private String dateCreated;

    protected Taxonomy(String id, String name, String dateCreated) {
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public List<Category> getCategories() {
        return TaxonomyDb.getCategoriesForTaxonomy(getId());
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

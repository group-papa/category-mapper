package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * A builder class which produces an immutable Taxonomy.
 */
public class TaxonomyBuilder {
    private String id;
    private String name;

    public TaxonomyBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TaxonomyBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public Taxonomy createTaxonomy() {
        return new Taxonomy(id, name);
    }
}
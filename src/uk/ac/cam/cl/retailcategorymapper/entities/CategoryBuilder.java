package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * A builder class which produces an immutable Category.
 */
public class CategoryBuilder {
    private String[] parts;
    private Taxonomy taxonomy;

    public CategoryBuilder setParts(String[] parts) {
        this.parts = parts;
        return this;
    }

    public CategoryBuilder setTaxonomy(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
        return this;
    }

    public Category createCategory() {
        return new Category(parts, taxonomy);
    }
}
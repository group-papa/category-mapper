package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * A builder class which produces an immutable Category.
 */
public class CategoryBuilder {
    private String id;
    private String[] parts;

    public CategoryBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public CategoryBuilder setParts(String[] parts) {
        this.parts = parts;
        return this;
    }

    public Category createCategory() {
        return new Category(id, parts);
    }
}

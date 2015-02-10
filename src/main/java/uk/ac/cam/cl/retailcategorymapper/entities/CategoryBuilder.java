package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * A builder class which produces an immutable Category.
 */
public class CategoryBuilder {
    private String[] parts;

    public CategoryBuilder setParts(String[] parts) {
        this.parts = parts;
        return this;
    }

    public Category createCategory() {
        return new Category(parts);
    }
}
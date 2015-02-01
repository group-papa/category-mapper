package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.HashMap;
import java.util.Map;

public class ProductBuilder {
    private String id;
    private String name;
    private String description = "";
    private Integer price = null;
    private Category originalCategory;
    private Map<String, String> attributes = new HashMap<String, String>();

    public ProductBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProductBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public ProductBuilder setPrice(Integer price) {
        this.price = price;
        return this;
    }

    public ProductBuilder setOriginalCategory(Category originalCategory) {
        this.originalCategory = originalCategory;
        return this;
    }

    public ProductBuilder addAttribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }

    public Product createProduct() {
        return new Product(id, name, description, price, originalCategory,
                           attributes);
    }
}
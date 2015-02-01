package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.Collections;
import java.util.Map;

public class Product {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Category originalCategory;
    private Map<String, String> attributes;

    protected Product(String id, String name, String description, Integer price,
                   Category originalCategory, Map<String, String> attributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalCategory = originalCategory;
        this.attributes = attributes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getPrice() {
        return price;
    }

    public Category getOriginalCategory() {
        return originalCategory;
    }

    public Map<String, String> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }
}

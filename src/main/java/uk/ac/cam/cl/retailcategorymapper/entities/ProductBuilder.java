package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * A builder class which produces an immutable Product.
 */
@JsonPOJOBuilder(buildMethodName = "createProduct")
public class ProductBuilder {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Category originalCategory;
    private Map<String, String> attributes = new HashMap<>();

    @JsonProperty("id")
    public ProductBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("name")
    public ProductBuilder setName(String name) {
        this.name = name;
        return this;
    }

    @JsonProperty("description")
    public ProductBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("price")
    public ProductBuilder setPrice(Integer price) {
        this.price = price;
        return this;
    }

    @JsonProperty("originalCategory")
    public ProductBuilder setOriginalCategory(Category originalCategory) {
        this.originalCategory = originalCategory;
        return this;
    }


    public ProductBuilder addAttribute(String key, String value) {
        this.attributes.put(key, value);
        return this;
    }

    public Product createProduct() {
        return new Product(id, name, description, price, originalCategory,       attributes);
    }
}

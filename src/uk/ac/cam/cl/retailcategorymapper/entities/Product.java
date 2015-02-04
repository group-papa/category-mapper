package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.Map;

/**
 * An immutable class to store a details about a product.
 */
public class Product {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Category originalCategory;
    private Map<String, String> attributes;

    /**
     * Generate an object representing a product. When field is not available, set the field to 
     * null. If the field is available, but is not filled, set any String fields to "" (the empty
     * string) and set the Integer price to -1 (an impossible value). We will presumably
     * always have the Category field filled. The Map will be set to an empty map if there are
     * no attributes provided and this was an option.
     */
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
    	return attributes;
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }
}

package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.Map;
import java.util.Set;

/**
 * An immutable class to store a details about a product.
 */
@JsonDeserialize(builder = ProductBuilder.class)
public class Product {
    private String id;
    private String name;
    private String description;
    private Integer price;
    private Category originalCategory;
    private Category destinationCategory = null;
    private Map<String, String> attributes;

    protected Product(String id, String name, String description, Integer price,
                   Category originalCategory, Category destinationCategory, Map<String, String>
            attributes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalCategory = originalCategory;
        this.destinationCategory = destinationCategory;
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

    public Category getDestinationCategory() { return destinationCategory; }

    public Set<String> getAttributeNames() {
        return attributes.keySet();
    }

    public String getAttribute(String key) {
        return attributes.get(key);
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Product)){return false;}
        Product p = (Product) o;

        if(!id.equals(p.id)){return false;}

        if((!name.equals(p.name))||
                (!description.equals(p.description))||
                (!price.equals(p.price))||
                (!originalCategory.equals(p.originalCategory))){
            throw new RuntimeException("product ID violation");
        }

        return true;
    }

    @Override
    public int hashCode(){
        int answer = 0;
        answer += id.hashCode();
        answer *=17;
        answer += name.hashCode();
        answer *=17;
        answer += price;
        return answer;
    }
}

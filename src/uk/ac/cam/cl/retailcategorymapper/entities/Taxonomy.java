package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.List;

/**
 * An immutable class to store details about a taxonomy.
 */
public class Taxonomy {
    private String id;
    private String name;

    protected Taxonomy(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Category> getCategories() {
        throw new UnsupportedOperationException();
    }
    
    @Override 
    public boolean equals(Object o){
    	//TODO change when getcategories becomes supported
    	if(!(o instanceof Taxonomy)){
    		return false;
    	}
    	Taxonomy t = (Taxonomy) o;
    	return ((id.equals(t.id))&&(name.equals(t.name)));
    }
    
    @Override 
    public int hashCode(){
    	//TODO change when getcategories becomes supported
    	return id.hashCode()+name.hashCode();
    }
    
    
    
}

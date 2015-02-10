package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.Arrays;

/**
 * An immutable class to store a details about a category.
 */
public class Category {
    private String[] parts;
    private Taxonomy taxonomy;
    
    /**
     * this.parts should not contain any null elements.
     */
    protected Category(String[] parts, Taxonomy taxonomy) {
        this.parts = Arrays.copyOf(parts, parts.length);
        this.taxonomy = taxonomy;
    }

    public String getPart(int n) {
        return parts[n];
    }

    public String[] getAllParts() {
        return Arrays.copyOf(parts, parts.length);
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    public String toString() {
    	return toString(" > ");
    } 
    
    public String toString(String split) {
    	if(parts.length==0){
    		return "the empty category";
    	}
    	String string = parts[0];
    	for(int i=1;i<parts.length;i++){
    		string = string.concat(split+parts[i]);
    	}
    	return string;
    }
    
    @Override
    public boolean equals(Object o){    	
    	if(o instanceof Category){    		
    		Category c = (Category) o;    		
    		if(c.parts.length==parts.length){
    			for(int i=0;i<parts.length;i++){
    				if(!parts[i].equals(c.parts[i])){
    					return false;
    				}
    			}
    			if((taxonomy==null)&&(c.taxonomy==null)){
    				return true;
    			}
    			if((taxonomy==null)||(c.taxonomy==null)){
    				return false;
    			}    			
    			return taxonomy.equals(c.taxonomy);
    		}    		
    	}    	
    	return false;
    }
    
    @Override
    public int hashCode(){
    	return parts.hashCode()+taxonomy.hashCode();
    }
    
}

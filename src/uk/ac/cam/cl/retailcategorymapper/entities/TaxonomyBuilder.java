package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.LinkedList;

/**
 * A builder class which produces an immutable Taxonomy.
 */
public class TaxonomyBuilder {
    private String name;
    private LinkedList<Category> categoryList = new LinkedList<>();
    private boolean used = false;
    
    public TaxonomyBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public TaxonomyBuilder addCategory(Category c){
    	categoryList.add(c);
    	return this;
    }
    
    public boolean isUsed(){
    	return used;
    }
    
		public Taxonomy createTaxonomy() {
	    	if(used){
	    		throw new RuntimeException("Attempt to reuse a TaxonomyBuilder");
	    	}
	    	used = true;
        return Taxonomy.makeTaxonomy(name,categoryList);
    }
}
package uk.ac.cam.cl.retailcategorymapper.entities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * An immutable class to store details about a taxonomy.
 */
public class Taxonomy {	

	private static HashMap<String, Taxonomy> instances = new HashMap<>();
	
	private String name;
	private LinkedList<Category> categoryList;

	private Taxonomy(String name, List<Category> categoryList) {

		if (instances.containsKey(name)) {
			throw new RuntimeException("Attempt to redefine a Taxonomy with same name in class Taxonomy ");
		}

		instances.put(name, this);
		// this creates a clone so that the calling method doesn't have a reference
		// on the internally mutable categoryList
		this.categoryList = new LinkedList<>(categoryList);
		this.name = name;
	}

	public static Taxonomy makeTaxonomy(String name, List<Category> categoryList) {
		if (instances.containsKey(name.hashCode())) {
			//not sure if we just want to return it here or throw exception
			throw new RuntimeException("Attempt to create Taxonomy when one already exists with that name");
		}
		return new Taxonomy(name,categoryList);
	}

	public static Taxonomy getTaxonomy(String name){
		if(instances.containsKey(name)){
			return instances.get(name);
		}
		/*
		 * an alternative would be to create the Taxonomy here but it would be empty
		 * since we dont havea category list and the class is immuatble
		 */
		throw new RuntimeException("attempt to get taxonomy not yet created");
	}

	public String getName() {
		return name;
	}

	/*
	 * getCategories returns a shallow copy of the category list this is fine
	 * since categories are immutable quite inefficient
	 */
	@SuppressWarnings("unchecked")
	public List<Category> getCategories() {
		return (List<Category>) categoryList.clone();
	}

	@Override
	public boolean equals(Object o) {
		//since its immutable and names are unique we can just check names
		if (!(o instanceof Taxonomy)) {
			return false;
		}
		Taxonomy t = (Taxonomy) o;
		return name.equals(t.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}

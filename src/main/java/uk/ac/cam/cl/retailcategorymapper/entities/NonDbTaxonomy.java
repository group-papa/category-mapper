package uk.ac.cam.cl.retailcategorymapper.entities;

import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;

import java.util.HashSet;
import java.util.Set;

public class NonDbTaxonomy extends Taxonomy {
	private Set<Category> categorySet;

	protected NonDbTaxonomy(String id, String name, String dateCreated) {
		super(id, name, dateCreated);
		this.categorySet = new HashSet<Category>();
	}
	public Set<Category> getCategories() {
		return this.categorySet;
	}
}

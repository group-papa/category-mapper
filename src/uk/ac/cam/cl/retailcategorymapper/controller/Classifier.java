package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

/**
 * An interface for all classifiers used by the category mapper.
 */
public interface Classifier {
	
	public void trainWithBagOfWords(Taxonomy taxonomy, Product product);
	
	public void trainWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight,
    		double destinationCategoryWeight);
	
    public Mapping classifyWithBagOfWords(Taxonomy taxonomy, Product product);
    
    public Mapping classifyWithWeights(Taxonomy taxonomy, Product product, double originalCategoryWeight,
    		double nameWeight, double descriptionWeight, double priceWeight);
}



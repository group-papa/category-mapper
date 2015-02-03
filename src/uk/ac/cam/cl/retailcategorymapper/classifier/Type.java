package uk.ac.cam.cl.retailcategorymapper.classifier;

/**
 * This enum represents where the information came from. It will be used to distinguish a feature
 * as originating from the original category, the product name, product description, product price,
 * or product google category
 * @author tatsianaivonchyk
 *
 */
public enum Type {
	ORIGINALCATEGORY, NAME, DESCRIPTION, PRICE, GOOGLECATEGORY
}

package uk.ac.cam.cl.retailcategorymapper.feature;

/**
 * This enum represents where the information came from. It will be used to distinguish a feature
 * as originating from the original category, the product name, product description, product price,
 * or desination category
 * @author tatsianaivonchyk
 *
 */
public enum FeatureType {
	ORIGINALCATEGORY, ID, NAME, DESCRIPTION, PRICE, ATTRIBUTES, DESTINATIONCATEGORY
}

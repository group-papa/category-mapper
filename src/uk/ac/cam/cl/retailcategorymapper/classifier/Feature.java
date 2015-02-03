package uk.ac.cam.cl.retailcategorymapper.classifier;

/**
 * Class to represent the features that the classifier will use. 
 * @author tatsianaivonchyk
 *
 */
public class Feature {
	
	private final Type type;
	private final String featureString;
	
	public Feature(Type type, String featureString) {
		this.type = type;
		this.featureString = featureString;
	}
	
}

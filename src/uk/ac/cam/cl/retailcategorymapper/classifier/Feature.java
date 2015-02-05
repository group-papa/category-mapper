package uk.ac.cam.cl.retailcategorymapper.classifier;

/**
 * Class to represent the features that the classifier will use. 
 * @author tatsianaivonchyk
 *
 */
public class Feature {
	
	private final FeatureType type;
	private final String featureString;
	
	public Feature(FeatureType type, String featureString) {
		this.type = type;
		this.featureString = featureString;
	}
	
}

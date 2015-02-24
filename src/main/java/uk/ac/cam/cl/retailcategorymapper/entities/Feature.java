package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent the features that the classifier will use.
 */
public class Feature {
    private final FeatureSource source;
    private final String featureString;

    @JsonCreator
    public Feature(@JsonProperty("source") FeatureSource source,
                   @JsonProperty("featureString") String featureString) {
        this.source = source;
        this.featureString = featureString;
    }

    public FeatureSource getSource(){
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature)) return false;

        Feature feature = (Feature) o;

        if (!featureString.equals(feature.featureString)) return false;
        if (source != feature.source) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + featureString.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "<" + source.toString() + ": " + featureString + ">";
    }
}

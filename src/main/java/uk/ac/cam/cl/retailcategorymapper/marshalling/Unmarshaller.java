package uk.ac.cam.cl.retailcategorymapper.marshalling;

import java.util.List;

/**
 * Interface for all unmarshallers. Unmarshallers take a String input and
 * produce a list of Ts.
 */
public interface Unmarshaller<T> {
    public List<T> unmarshal(String data);
}

package uk.ac.cam.cl.retailcategorymapper.marshalling;

/**
 * Interface for all unmarshallers. Unmarshallers take a F input and produce
 * a T output.
 */
public interface Unmarshaller<F, T> {
    public T unmarshal(F data);
}

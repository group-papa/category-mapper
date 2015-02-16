package uk.ac.cam.cl.retailcategorymapper.marshalling;

/**
 * Interface for all marshallers. Marshallers take an F and produce a T output.
 */
public interface Marshaller<F, T> {
    public T marshal(F data);
}

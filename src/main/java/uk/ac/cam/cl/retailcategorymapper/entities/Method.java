package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * This enum represents the method by which a mapping was found.
 */
public enum Method {
    MANUAL, // Manually categorised
    CLASSIFIED, // Calculated by the classifier
    UPLOAD // Provided in uploaded file
}

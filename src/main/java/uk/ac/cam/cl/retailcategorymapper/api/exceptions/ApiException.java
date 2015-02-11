package uk.ac.cam.cl.retailcategorymapper.api.exceptions;

/**
 * A base exception for use within the API.
 */
public abstract class ApiException extends Exception {
    public ApiException(String message) {
        super(message);
    }

    public abstract int getStatusCode();
}

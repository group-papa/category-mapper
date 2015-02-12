package uk.ac.cam.cl.retailcategorymapper.api.exceptions;

/**
 * Exception thrown when a resource cannot be found. This should result in a
 * 404 status code being returned.
 */
public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}

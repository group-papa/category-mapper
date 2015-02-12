package uk.ac.cam.cl.retailcategorymapper.api.exceptions;

/**
 * Exception thrown when an invalid input was supplied. This should result in a
 * 400 status code being returned.
 */
public class BadInputException extends ApiException {
    public BadInputException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}

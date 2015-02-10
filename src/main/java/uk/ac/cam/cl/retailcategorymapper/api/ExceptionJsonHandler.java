package uk.ac.cam.cl.retailcategorymapper.api;

import com.google.gson.Gson;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

/**
 * Convert an exception into a JSON formatted error message.
 */
public class ExceptionJsonHandler implements ExceptionHandler {
    private Gson gson = new Gson();

    @Override
    public void handle(Exception exception, Request request, Response response) {
        response.status(400);
        response.type("application/json");

        ErrorResponse error = new ErrorResponse(exception.getMessage());
        response.body(gson.toJson(error));
    }

    class ErrorResponse {
        String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }
}

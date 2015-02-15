package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;

/**
 * Delete an upload.
 */
public class DeleteUploadRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String uploadId = request.params(":upload[id]");

        boolean result = UploadDb.deleteUpload(uploadId);
        if (!result) {
            throw new NotFoundException("Unknown upload ID.");
        }

        return null;
    }
}

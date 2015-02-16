package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

/**
 * Get metadata for a specific taxonomy.
 */
public class GetUploadRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String uploadId = request.params(":upload[id]");

        Upload upload = UploadDb.getUpload(uploadId);
        if (upload == null) {
            throw new NotFoundException("Unknown upload ID.");
        }

        return generateGetUploadReply(upload);
    }

    static GetUploadReply generateGetUploadReply(Upload upload) {
        return new GetUploadReply(upload);
    }

    static class GetUploadReply {
        Upload upload;

        public GetUploadReply(Upload upload) {
            this.upload = upload;
        }
    }
}

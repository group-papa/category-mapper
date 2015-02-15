package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.List;

/**
 * List uploads stored in the database.
 */
public class ListUploadsRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        List<Upload> results = UploadDb.getUploads();
        return new ListUploadsReply(results);
    }

    static class ListUploadsReply {
        List<Upload> uploads;

        public ListUploadsReply(List<Upload> uploads) {
            this.uploads = uploads;
        }
    }
}

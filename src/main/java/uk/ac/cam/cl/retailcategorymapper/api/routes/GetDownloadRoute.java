package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import spark.Route;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.DownloadDb;

/**
 * Get the results from a previous classification.
 */
public class GetDownloadRoute implements Route {
    @Override
    public Object handle(Request request, Response response)
            throws Exception {
        String downloadId = request.params(":download[id]");

        String download = DownloadDb.getDownload(downloadId);
        if (download == null) {
            throw new NotFoundException("Unknown download ID.");
        }

        response.type("application/xml");
        response.header("Access-Control-Allow-Origin", "*");

        return download;
    }
}

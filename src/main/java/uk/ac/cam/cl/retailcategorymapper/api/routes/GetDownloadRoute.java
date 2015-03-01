package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import spark.Route;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.DownloadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Download;
import uk.ac.cam.cl.retailcategorymapper.marshalling.MappingXmlMarshaller;

import java.util.ArrayList;

/**
 * Get the results from a previous classification.
 */
public class GetDownloadRoute implements Route {
    @Override
    public Object handle(Request request, Response response)
            throws Exception {
        String downloadId = request.params(":download[id]");

        Download download = DownloadDb.getDownload(downloadId);
        if (download == null) {
            throw new NotFoundException("Unknown download ID.");
        }

        MappingXmlMarshaller marshaller = new MappingXmlMarshaller();
        String xml = marshaller.marshal(
                new ArrayList<>(download.getMappings().values()));

        response.type("application/xml");
        response.header("Access-Control-Allow-Origin", "*");

        return xml;
    }
}

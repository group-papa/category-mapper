package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.controller.Controller;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainResponse;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.ArrayList;
import java.util.List;

/**
 * Classify a set of products from an upload.
 */
public class TrainRoute extends BaseApiRoute {
    private final Controller controller = new Controller();

    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String taxonomyId = request.queryParams("taxonomy[id]");
        Taxonomy taxonomy = TaxonomyDb.getTaxonomy(taxonomyId);
        if (taxonomy == null) {
            throw new NotFoundException("Unknown taxonomy ID.");
        }

        String uploadId = request.queryParams("upload[id]");
        Upload upload = UploadDb.getUpload(uploadId);
        if (upload == null) {
            throw new NotFoundException("Unknown upload ID.");
        }

        List<Mapping> mappings = new ArrayList<>(upload.getMappings().values());

        TrainRequest trainRequest = new TrainRequest(
                taxonomy, mappings, true, true);

        TrainResponse result = controller.train(trainRequest);

        return new TrainReply(result.getTrainCountManual(),
                result.getTrainCountClassifier());
    }

    static class TrainReply {
        int trainCountManual;
        int trainCountClassifier;

        public TrainReply(int trainCountManual, int trainCountClassifier) {
            this.trainCountManual = trainCountManual;
            this.trainCountClassifier = trainCountClassifier;
        }
    }
}

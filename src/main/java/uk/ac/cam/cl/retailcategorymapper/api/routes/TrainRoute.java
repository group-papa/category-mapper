package uk.ac.cam.cl.retailcategorymapper.api.routes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.BadInputException;
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
 * Train the mapping engine from an upload.
 */
public class TrainRoute extends BaseApiRoute {
    private final Controller controller = new Controller();
    private final Gson gson = new Gson();

    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        InputJson inputJson;
        try {
            inputJson = gson.fromJson(request.body(), InputJson.class);
        } catch (JsonSyntaxException e) {
            throw new BadInputException("Invalid JSON provided.");
        }

        String taxonomyId = inputJson.taxonomyId;
        Taxonomy taxonomy = TaxonomyDb.getTaxonomy(taxonomyId);
        if (taxonomy == null) {
            throw new NotFoundException("Unknown taxonomy ID.");
        }

        String uploadId = inputJson.uploadId;
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

    static class InputJson {
        @SerializedName("taxonomy[id]")
        String taxonomyId;
        @SerializedName("upload[id]")
        String uploadId;
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

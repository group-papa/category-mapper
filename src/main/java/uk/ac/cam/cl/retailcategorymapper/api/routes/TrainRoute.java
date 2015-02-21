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
import java.util.Map;
import java.util.stream.Collectors;

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
            throw new BadInputException("Invalid request.");
        }
        if (inputJson == null) {
            throw new BadInputException("Invalid request.");
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

        List<Mapping> mappings;
        if (inputJson.productIds == null || inputJson.productIds.size() == 0) {
            mappings = new ArrayList<>(upload.getMappings().values());
        } else {
            Map<String, Mapping> uploadedMappings = upload.getMappings();
            mappings = new ArrayList<>();
            mappings.addAll(inputJson.productIds.stream()
                    .filter(uploadedMappings::containsKey)
                    .map(uploadedMappings::get).collect(Collectors.toList()));
        }

        TrainRequest trainRequest = new TrainRequest(taxonomy, mappings,
                inputJson.addToManualMappings, inputJson.addToTrainingSet);

        TrainResponse result = controller.train(trainRequest);

        return new TrainReply(result.getTrainCountManual(),
                result.getTrainCountClassifier());
    }

    static class InputJson {
        @SerializedName("taxonomy[id]")
        String taxonomyId;
        @SerializedName("upload[id]")
        String uploadId;
        @SerializedName("products")
        List<String> productIds;
        @SerializedName("addToManualMappings")
        boolean addToManualMappings;
        @SerializedName("addToTrainingSet")
        boolean addToTrainingSet;
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

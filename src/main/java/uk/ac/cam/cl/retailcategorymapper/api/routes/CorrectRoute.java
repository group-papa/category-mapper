package uk.ac.cam.cl.retailcategorymapper.api.routes;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.BadInputException;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.controller.Controller;
import uk.ac.cam.cl.retailcategorymapper.db.DownloadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Download;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainResponse;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * Make a correction to a previous classification.
 */
public class CorrectRoute extends BaseApiRoute {
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

        String downloadId = inputJson.downloadId;
        Download download = DownloadDb.getDownload(downloadId);
        if (download == null) {
            throw new NotFoundException("Unknown download ID.");
        }

        Map<String, Mapping> mappings = download.getMappings();
        String productId = inputJson.productId;
        Mapping mapping = mappings.get(productId);
        if (mapping == null) {
            throw new NotFoundException("Unknown product ID.");
        }

        Taxonomy taxonomy = mapping.getTaxonomy();

        Set<Category> categories = taxonomy.getCategories();
        Category correctCategory = null;
        for (Category category : categories) {
            if (category.getId().equals(inputJson.categoryId)) {
                correctCategory = category;
            }
        }
        if (correctCategory == null) {
            throw new NotFoundException("Unknown category ID.");
        }

        Mapping correctedMapping = new MappingBuilder()
                .setProduct(mapping.getProduct())
                .setTaxonomy(taxonomy)
                .setCategory(correctCategory)
                .setMethod(Method.MANUAL)
                .setConfidence(Double.MAX_VALUE)
                .createMapping();

        mappings.put(correctedMapping.getProduct().getId(), correctedMapping);
        DownloadDb.setDownload(download);

        TrainResponse trainResponse = controller.train(new TrainRequest(
                taxonomy,
                Arrays.asList(correctedMapping),
                inputJson.addToManualMappings,
                inputJson.addToTrainingSet
        ));

        return new TrainReply(trainResponse.getTrainCountManual(),
                trainResponse.getTrainCountClassifier());
    }

    static class InputJson {
        @SerializedName("download[id]")
        String downloadId;
        @SerializedName("product[id]")
        String productId;
        @SerializedName("category[id]")
        String categoryId;
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

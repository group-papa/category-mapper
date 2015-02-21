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
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyResponse;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Classify a set of products from an upload.
 */
public class ClassifyRoute extends BaseApiRoute {
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

        List<Product> products;
        if (inputJson.productIds == null || inputJson.productIds.size() == 0) {
            products = new ArrayList<>(upload.getProducts().values());
        } else {
            Map<String, Product> uploadedProducts = upload.getProducts();
            products = new ArrayList<>();
            products.addAll(inputJson.productIds.stream()
                    .filter(uploadedProducts::containsKey)
                    .map(uploadedProducts::get).collect(Collectors.toList()));
        }

        ClassifyRequest classifyRequest = new ClassifyRequest(
                taxonomy, products);

        ClassifyResponse classifyResponse = controller.classify(classifyRequest);

        List<MappingResult> results = new ArrayList<>();
        for (Map.Entry<Product, List<Mapping>> mapping : classifyResponse
                .getMappings().entrySet()) {
            Product product = mapping.getKey();

            List<MappingEntry> mappings = mapping.getValue().stream()
                    .map(m -> new MappingEntry(
                            m.getCategory().getId(),
                            m.getMethod(),
                            m.getConfidence())).collect(Collectors.toList());

            results.add(new MappingResult(
                    product.getId(),
                    product.getName(),
                    product.getOriginalCategory(),
                    mappings
            ));
        }

        return new ClassifyReply(results);
    }

    static class InputJson {
        @SerializedName("taxonomy[id]")
        String taxonomyId;
        @SerializedName("upload[id]")
        String uploadId;
        @SerializedName("products")
        List<String> productIds;
    }

    static class ClassifyReply {
        List<MappingResult> products;

        public ClassifyReply(List<MappingResult> products) {
            this.products = products;
        }
    }

    static class MappingResult {
        private String productId;
        private String productName;
        private Category productOriginalCategory;
        private List<MappingEntry> mappings;

        public MappingResult(String productId, String productName,
                             Category productOriginalCategory,
                             List<MappingEntry> mappings) {
            this.productId = productId;
            this.productName = productName;
            this.productOriginalCategory = productOriginalCategory;
            this.mappings = mappings;
        }
    }

    static class MappingEntry {
        private String categoryId;
        private Method method;
        private double confidence;

        public MappingEntry(String categoryId, Method method,
                            double confidence) {
            this.categoryId = categoryId;
            this.method = method;
            this.confidence = confidence;
        }
    }
}

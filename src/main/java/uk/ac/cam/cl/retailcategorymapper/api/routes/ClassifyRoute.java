package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
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

        List<Product> products = new ArrayList<>(upload.getProducts().values());

        ClassifyRequest classifyRequest = new ClassifyRequest(
                taxonomy, products);

        ClassifyResponse classifyResponse = controller.classify(classifyRequest);

        List<MappingResult> results = new ArrayList<>();
        for (Map.Entry<Product, List<Mapping>> mapping : classifyResponse
                .getMappings().entrySet()) {
            Product product = mapping.getKey();

            List<MappingEntry> mappings = mapping.getValue().stream()
                    .map(m -> new MappingEntry(m.getCategory(), m.getMethod(),
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

    static class ClassifyReply {
        List<MappingResult> mappings;

        public ClassifyReply(List<MappingResult> mappings) {
            this.mappings = mappings;
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
        private Category category;
        private Method method;
        private float confidence;

        public MappingEntry(Category category, Method method,
                            float confidence) {
            this.category = category;
            this.method = method;
            this.confidence = confidence;
        }
    }
}

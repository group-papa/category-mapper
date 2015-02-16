package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.NotFoundException;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;

import java.util.Map;

/**
 * Get a product from a specific upload.
 */
public class GetUploadedProductRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response)
            throws Exception {
        String uploadId = request.params(":upload[id]");

        Upload upload = UploadDb.getUpload(uploadId);
        if (upload == null) {
            throw new NotFoundException("Unknown upload ID.");
        }

        String productId = request.params(":product[id]");
        Map<String, Product> products = UploadDb.getUploadProducts(upload);
        Product product = products.get(productId);
        if (product == null) {
            throw new NotFoundException("Unknown product ID.");
        }

        return new GetUploadedProductReply(
                new ProductEntry(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice(),
                        product.getOriginalCategory().getAllParts()
                ));
    }

    static class GetUploadedProductReply {
        ProductEntry product;

        public GetUploadedProductReply(ProductEntry product) {
            this.product = product;
        }
    }

    static class ProductEntry {
        private String id;
        private String name;
        private String description;
        private int price;
        private String[] originalCategory;

        public ProductEntry(String id, String name,
                            String description, int price,
                            String[] originalCategory) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.originalCategory = originalCategory;
        }
    }
}

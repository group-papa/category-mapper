package uk.ac.cam.cl.retailcategorymapper.api.routes;

import spark.Request;
import spark.Response;
import uk.ac.cam.cl.retailcategorymapper.api.exceptions.BadInputException;
import uk.ac.cam.cl.retailcategorymapper.api.formdata.FormDataField;
import uk.ac.cam.cl.retailcategorymapper.api.formdata.FormDataParser;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;
import uk.ac.cam.cl.retailcategorymapper.entities.UploadBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlProductUnmarshaller;
import uk.ac.cam.cl.retailcategorymapper.utils.DateTime;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Add a new upload to the database.
 */
public class AddUploadRoute extends BaseApiRoute {
    @Override
    public Object handleRequest(Request request, Response response) throws Exception {
        FormDataParser formDataParser = new FormDataParser(request.raw());

        FormDataField attachment = formDataParser.getField
                ("upload[attachment]");
        if (attachment == null) {
            throw new BadInputException("A file must be provided.");
        }

        XmlProductUnmarshaller productUnmarshaller = new
                XmlProductUnmarshaller();
        XmlMappingUnmarshaller mappingUnmarshaller = new
                XmlMappingUnmarshaller();

        List<Product> products = productUnmarshaller.unmarshal(
                attachment.getContents());
        List<Mapping> mappings = mappingUnmarshaller.unmarshal(
                attachment.getContents());

        Map<String, Product> productsMap = new HashMap<>();
        for (Product p : products) {
            productsMap.put(p.getId(), p);
        }
        Map<String, Mapping> mappingsMap = new HashMap<>();
        for (Mapping m : mappings) {
            mappingsMap.put(m.getProduct().getId(), m);
        }

        Upload upload = new UploadBuilder()
                .setId(Uuid.generateUUID())
                .setFilename(attachment.getName())
                .setDateCreated(DateTime.getCurrentTimeIso8601())
                .setProductCount(products.size())
                .setMappingCount(mappings.size())
                .createUpload();

        UploadDb.setUpload(upload, productsMap, mappingsMap);

        return GetUploadRoute.generateGetUploadReply(upload);
    }
}

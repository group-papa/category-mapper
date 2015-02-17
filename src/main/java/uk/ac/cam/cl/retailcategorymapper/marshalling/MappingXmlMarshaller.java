package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.List;

public class MappingXmlMarshaller implements Marshaller<List<Mapping>, String> {
    private static final String NAME_TAG = "productName";
    private static final String PRODUCT_TAG = "product";
    private static final String ID_TAG = "productSku";
    private static final String DESCRIPTION_TAG = "productDescription";
    private static final String PRICE_TAG = "productPrice";
    private static final String CATEGORY_TAG = "productCategory";
    private static final String MAPPED_CATEGORY_TAG = "productGoogleCategory";

    @Override
    public String marshal(List<Mapping> mappings) {
        // TODO: Rewrite using XML library, this is dangerous.
        StringBuilder answerBuilder = new StringBuilder();

        answerBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        answerBuilder.append("<products>");
        for (Mapping m : mappings) {
            Product p = m.getProduct();

            answerBuilder.append("<" + PRODUCT_TAG + ">");

            answerBuilder.append("<" + NAME_TAG + ">");
            answerBuilder.append(p.getName());
            answerBuilder.append("</" + NAME_TAG + ">");

            answerBuilder.append("<" + ID_TAG + ">");
            answerBuilder.append(p.getId());
            answerBuilder.append("</" + ID_TAG + ">");

            answerBuilder.append("<" + DESCRIPTION_TAG + ">");
            answerBuilder.append(p.getDescription());
            answerBuilder.append("</" + DESCRIPTION_TAG + ">");

            answerBuilder.append("<" + PRICE_TAG + ">");
            answerBuilder.append((double) (p.getPrice()) / 100.0);
            // TODO: Format the price
            answerBuilder.append("</" + PRICE_TAG + ">");

            answerBuilder.append("<" + DESCRIPTION_TAG + ">");
            answerBuilder.append(p.getDescription());
            answerBuilder.append("</" + DESCRIPTION_TAG + ">");

            answerBuilder.append("<" + CATEGORY_TAG + ">");
            answerBuilder.append(p.getOriginalCategory().toString());
            answerBuilder.append("</" + CATEGORY_TAG + ">");

            answerBuilder.append("<" + MAPPED_CATEGORY_TAG + ">");
            answerBuilder.append(m.getCategory().toString());
            answerBuilder.append("</" + MAPPED_CATEGORY_TAG + ">");

            answerBuilder.append("</" + PRODUCT_TAG + ">");
        }
        answerBuilder.append("</products>");

        return answerBuilder.toString();
    }
}

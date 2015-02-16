package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.List;

/**
 * Marshal a list of products into XML.
 */
public class ProductXmlMarshaller implements Marshaller<List<Product>, String> {
    private static final String NAME_TAG = "productName";
    private static final String PRODUCT_TAG = "product";
    private static final String ID_TAG = "productSku";
    private static final String DESCRIPTION_TAG = "productDescription";
    private static final String PRICE_TAG = "productPrice";
    private static final String CATEGORY_TAG = "productCategory";

    @Override
    public String marshal(List<Product> data) {
        // TODO: Rewrite using XML library.
        StringBuilder answerBuilder = new StringBuilder();

        answerBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        answerBuilder.append("<products>");

        for (Product product : data) {
            answerBuilder.append("<" + PRODUCT_TAG + ">");

            answerBuilder.append("<" + NAME_TAG + ">");
            answerBuilder.append(product.getName());
            answerBuilder.append("</" + NAME_TAG + ">");

            answerBuilder.append("<" + ID_TAG + ">");
            answerBuilder.append(product.getId());
            answerBuilder.append("</" + ID_TAG + ">");

            answerBuilder.append("<" + DESCRIPTION_TAG + ">");
            answerBuilder.append(product.getDescription());
            answerBuilder.append("</" + DESCRIPTION_TAG + ">");

            answerBuilder.append("<" + PRICE_TAG + ">");
            answerBuilder.append((double) (product.getPrice()) / 100.0);
            // TODO: Format the price
            answerBuilder.append("</" + PRICE_TAG + ">");

            answerBuilder.append("<" + DESCRIPTION_TAG + ">");
            answerBuilder.append(product.getDescription());
            answerBuilder.append("</" + DESCRIPTION_TAG + ">");

            answerBuilder.append("<" + CATEGORY_TAG + ">");
            answerBuilder.append(product.getOriginalCategory().toString());
            answerBuilder.append("</" + CATEGORY_TAG + ">");

            answerBuilder.append("</" + PRODUCT_TAG + ">");
        }

        answerBuilder.append("</products>");

        return answerBuilder.toString();
    }
}

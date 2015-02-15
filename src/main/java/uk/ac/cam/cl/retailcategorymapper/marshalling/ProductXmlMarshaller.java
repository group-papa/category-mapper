package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.util.List;

/**
 * Marshal a list of products into XML.
 */
public class ProductXmlMarshaller implements Marshaller<List<Product>, String> {
    private static final String NAME_XML = "productName";
    private static final String PRODUCT_XML = "product";
    private static final String ID_XML = "productSku";
    private static final String DESCRIPTION_XML = "productDescription";
    private static final String PRICE_XML = "productPrice";
    private static final String CATEGORY_XML = "productCategory";

    @Override
    public String marshal(List<Product> data) {
        // TODO: Rewrite using XML library.
        StringBuilder answerBuilder = new StringBuilder();

        answerBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        answerBuilder.append("<products>");

        for (Product product : data) {
            answerBuilder.append("<" + PRODUCT_XML + ">");

            answerBuilder.append("<" + NAME_XML + ">");
            answerBuilder.append(product.getName());
            answerBuilder.append("</" + NAME_XML + ">");

            answerBuilder.append("<" + ID_XML + ">");
            answerBuilder.append(product.getId());
            answerBuilder.append("</" + ID_XML + ">");

            answerBuilder.append("<" + DESCRIPTION_XML + ">");
            answerBuilder.append(product.getDescription());
            answerBuilder.append("</" + DESCRIPTION_XML + ">");

            answerBuilder.append("<" + PRICE_XML + ">");
            answerBuilder.append((double) (product.getPrice()) / 100.0);
            // TODO: Format the price
            answerBuilder.append("</" + PRICE_XML + ">");

            answerBuilder.append("<" + DESCRIPTION_XML + ">");
            answerBuilder.append(product.getDescription());
            answerBuilder.append("</" + DESCRIPTION_XML + ">");

            answerBuilder.append("<" + CATEGORY_XML + ">");
            answerBuilder.append(product.getOriginalCategory().toString());
            answerBuilder.append("</" + CATEGORY_XML + ">");

            answerBuilder.append("</" + PRODUCT_XML + ">");
        }

        answerBuilder.append("</products>");

        return answerBuilder.toString();
    }
}

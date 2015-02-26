package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

/**
 * Unmarshal an XML element into a product.
 */
public class XmlProductElementUnmarshaller implements Unmarshaller<Element,
        Product> {
    private static final String NAME_TAG = "productName";
    private static final String ID_TAG = "productSku";
    private static final String DESCRIPTION_TAG = "productDescription";
    private static final String PRICE_TAG = "productPrice";
    private static final String CATEGORY_TAG = "productCategory";

    private CategoryUnmarshaller categoryUnmarshaller = new
            CategoryUnmarshaller();

    @Override
    public Product unmarshal(Element element) {
        ProductBuilder productBuilder = new ProductBuilder();

        String price = element.getElementsByTagName(PRICE_TAG).item(0).getTextContent();
        productBuilder.setPrice((int) (Math.round(Double.valueOf(price) * 100.0)));
        productBuilder.setId(element.getElementsByTagName(ID_TAG).item(0).getTextContent());
        productBuilder.setName(element.getElementsByTagName(NAME_TAG).item(0).getTextContent());
        productBuilder.setDescription(element.getElementsByTagName(DESCRIPTION_TAG).item(0).getTextContent());
        String categoryString = element.getElementsByTagName(CATEGORY_TAG).item(0).getTextContent();
        productBuilder.setOriginalCategory(categoryUnmarshaller.unmarshal(categoryString));

        return productBuilder.createProduct();
    }
}

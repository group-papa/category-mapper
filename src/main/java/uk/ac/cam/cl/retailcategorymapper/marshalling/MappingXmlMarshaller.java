package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.List;

public class MappingXmlMarshaller implements Marshaller<List<Mapping>, String> {
    private static final String PRODUCTS_TAG = "products";
    private static final String PRODUCT_TAG = "product";
    private static final String NAME_TAG = "productName";
    private static final String ID_TAG = "productSku";
    private static final String DESCRIPTION_TAG = "productDescription";
    private static final String PRICE_TAG = "productPrice";
    private static final String CATEGORY_TAG = "productCategory";
    private static final String MAPPED_CATEGORY_TAG = "productGoogleCategory";

    private DecimalFormat priceFormat = new DecimalFormat("#.00");

    @Override
    public String marshal(List<Mapping> mappings) {
        DocumentBuilderFactory documentBuilderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
        try {
            documentBuilder = documentBuilderFactory
                    .newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return "";
        }

        Document document = documentBuilder.newDocument();
        Element rootElement = document.createElement(PRODUCTS_TAG);

        for (Mapping mapping : mappings) {
            Product product = mapping.getProduct();

            Element productElement = document.createElement(PRODUCT_TAG);

            Element name = document.createElement(NAME_TAG);
            name.appendChild(document.createTextNode(product.getName()));
            productElement.appendChild(name);

            Element id = document.createElement(ID_TAG);
            id.appendChild(document.createTextNode(product.getId()));
            productElement.appendChild(id);

            Element description = document.createElement(DESCRIPTION_TAG);
            description.appendChild(document.createTextNode(
                    product.getDescription()));
            productElement.appendChild(description);

            Element price = document.createElement(PRICE_TAG);
            price.appendChild(document.createTextNode(
                    priceFormat.format(product.getPrice() / 100.0)
            ));
            productElement.appendChild(price);

            Element category = document.createElement(CATEGORY_TAG);
            category.appendChild(document.createTextNode(
                    product.getOriginalCategory().toString()));
            productElement.appendChild(category);

            Element mappedCategory = document.createElement(
                    MAPPED_CATEGORY_TAG);
            mappedCategory.appendChild(document.createTextNode(
                    mapping.getCategory().toString()));
            productElement.appendChild(mappedCategory);

            rootElement.appendChild(productElement);
        }

        document.appendChild(rootElement);

        TransformerFactory transformerFactory = TransformerFactory
                .newInstance();
        Transformer transformer;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
            return "";
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        DOMSource domSource = new DOMSource(document);
        StringWriter stringWriter = new StringWriter();
        StreamResult streamResult = new StreamResult(stringWriter);
        try {
            transformer.transform(domSource, streamResult);
        } catch (TransformerException e) {
            e.printStackTrace();
            return "";
        }

        return stringWriter.toString();
    }
}

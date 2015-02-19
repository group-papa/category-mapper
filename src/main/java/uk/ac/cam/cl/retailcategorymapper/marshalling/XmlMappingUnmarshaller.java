package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

/**
 * Unmarshal XML into a list of mappings.
 */
public class XmlMappingUnmarshaller implements Unmarshaller<String,
        List<Mapping>> {
    private static final String PRODUCT_TAG = "product";
    private static final String MAPPED_CATEGORY_TAG = "productGoogleCategory";

    private XmlProductElementUnmarshaller xmlProductElementUnmarshaller = new
            XmlProductElementUnmarshaller();
    private CategoryUnmarshaller categoryUnmarshaller = new
            CategoryUnmarshaller();

    @Override
    public List<Mapping> unmarshal(String data) {
        List<Mapping> result = new LinkedList<>();

        InputSource inputSource = new InputSource(new StringReader(data));
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder;
        try {
            dBuilder = dbFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return result;
        }

        Document doc;
        try {
            doc = dBuilder.parse(inputSource);
        } catch (IOException | SAXException e) {
            return result;
        }
        doc.getDocumentElement().normalize();

        NodeList nList = doc.getElementsByTagName(PRODUCT_TAG);
        for (int i = 0; i < nList.getLength(); i++) {
            Node nNode = nList.item(i);

            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) nNode;
                Product product = xmlProductElementUnmarshaller
                        .unmarshal(element);
                if (product == null) {
                    continue;
                }

                Node mappedCategoryNode = element.getElementsByTagName
                        (MAPPED_CATEGORY_TAG).item(0);
                if (mappedCategoryNode == null) {
                    continue;
                }
                Category mappedCategory = categoryUnmarshaller.unmarshal
                        (mappedCategoryNode.getTextContent());

                Mapping mapping = new MappingBuilder()
                        .setProduct(product)
                        .setCategory(mappedCategory)
                        .setMethod(Method.UPLOAD)
                        .setConfidence(Double.MAX_VALUE)
                        .createMapping();

                result.add(mapping);
            }
        }

        return result;
    }
}

package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

    /*
     * Authored by Charlie Barton
     *
     * Most of the parsing code was copied from:
     * http://stackoverflow.com/questions
     * /13786607/normalization-in-dom-parsing-with-java-how-does-it-work
     *
     * (under an open-source licence) so I don't really understand what it is doing but it
     * seems to work :)
     *
     * the constant strings at the top specify the format of the XML file
     */

public class XMLParser {

    static final String nameXML = "productName";
    static final String productXML = "product";
    static final String idXML = "productSku";
    static final String descriptionXML = "productDescription";
    static final String splitXML = " > ";
    static final String mappedCategoryXML = "productGoogleCategory";
    static final String priceXML = "productPrice";
    static final String categoryXML = "productCategory";


    static public List<Mapping> parseMapping(String stringXML) {
        try {
            InputSource inputSource = new InputSource(new StringReader(stringXML));
            List<Mapping> answer = new LinkedList<>();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(inputSource);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName(productXML);

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    ProductBuilder productBuild = prepareProductBuilder(element);
                    Product p = productBuild.createProduct();
                    MappingBuilder mapBuild = new MappingBuilder();
                    mapBuild.setProduct(p);

                    String s;
                    s = element.getElementsByTagName(mappedCategoryXML).item(0).getTextContent();
                    mapBuild.setCategory(prepareCategory(s));
                    mapBuild.setMethod(Method.UPLOAD);

                    answer.add(mapBuild.createMapping());
                }
            }
            return answer;
        }
        //re-throw as RuntimeExceptions
        catch (SAXException e) {
            System.err.println("SAX Exception in XML Parser");
            throw new RuntimeException("SAX Exception in XML Parser");
        } catch (IOException e) {
            System.err.println("IOException in XML Parser");
            throw new RuntimeException("IOException in XML Parser");
        } catch (ParserConfigurationException e) {
            System.err.println("SAX Exceptionin XML Parser");
            throw new RuntimeException("ParserConfigurationException in XML Parser");
        }
    }

    static public List<Product> parseProductList(String stringXML) {
        try {
            InputSource inputSource = new InputSource(new StringReader(stringXML));
            List<Product> answer = new LinkedList<>();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(inputSource);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName(productXML);

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    ProductBuilder productBuild = prepareProductBuilder((Element) nNode);
                    answer.add(productBuild.createProduct());
                }
            }
            return answer;
        }
        //re-throw as RuntimeExceptions
        catch (SAXException e) {
            System.err.println("SAX Exception in XML Parser");
            throw new RuntimeException("SAX Exception in XML Parser");
        } catch (IOException e) {
            System.err.println("IOException in XML Parser");
            throw new RuntimeException("IOException in XML Parser");
        } catch (ParserConfigurationException e) {
            System.err.println("SAX Exceptionin XML Parser");
            throw new RuntimeException("ParserConfigurationException in XML Parser");
        }
    }

    /*
     * this method takes an element and a hashMap and extracts the relevant fields
     * out of the element to build a product which it returns
     */
    static ProductBuilder prepareProductBuilder(Element element) {

        ProductBuilder productBuild = new ProductBuilder();

        String price = element.getElementsByTagName(priceXML).item(0).getTextContent();
        productBuild.setPrice((int) (Double.valueOf(price) * 100));

        productBuild.setId(element.getElementsByTagName(idXML).item(0).getTextContent());

        productBuild.setName(element.getElementsByTagName(nameXML).item(0).getTextContent());

        productBuild.setDescription(element.getElementsByTagName(descriptionXML).item(0).getTextContent());

        String catPath = element.getElementsByTagName(categoryXML).item(0).getTextContent();
        productBuild.setOriginalCategory (prepareCategory(catPath));

        //currently we ignore the attribute field of Product

        return productBuild;
    }

    static Category prepareCategory(String catPath) {
        String[] asList = catPath.split(splitXML);
        CategoryBuilder catBuild = new CategoryBuilder();
        catBuild.setParts(asList);
        return catBuild.createCategory();
    }

}

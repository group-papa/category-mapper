package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.junit.Test;
import spark.utils.Assert;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;

import java.io.File;
import java.util.List;


public class XMLTest {

    public void checkCategory(Category c){
        Assert.notNull(c.getAllParts());
        for(String s:c.getAllParts()){
            Assert.isTrue(!s.equals(""),"Empty string in category path");
        }
    }

    @Test
    public void testXMLParseProductList(){

        StringBuilder testBuilder = new StringBuilder();

        testBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        testBuilder.append("<products>");
        testBuilder.append("<product>");
        testBuilder.append("<productName>TestName0</productName>");
        testBuilder.append("<productSku>65014</productSku>");
        testBuilder.append("<productDescription>TestDescription0</productDescription>");
        testBuilder.append("<productPrice>40.00</productPrice>");
        testBuilder.append("<productCategory>Clothing &gt; Mens &gt; Retro Shirts &amp; Jackets</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; Accessories &gt; Clothing &gt; Tops</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("<product>");
        testBuilder.append("<productName>LFC Mens White Heritage Track Top</productName>");
        testBuilder.append("<productSku>27014</productSku>");
        testBuilder.append("<productDescription>Look stylish in this retro white Mens Heritage Track Top.</productDescription>");
        testBuilder.append("<productPrice>10.01</productPrice>");
        testBuilder.append("<productCategory>Souvenirs &gt; General &gt; Badges &amp; Keyrings</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; Accessories</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("</products>");

        String testXML = testBuilder.toString();

        List<Product> products = XMLParser.parseProductList(testXML);

        Assert.isTrue(products.size() == 2,"product list of incorrect length");

        Product firstProduct = products.get(0);
        Assert.isTrue(firstProduct.getName().equals("TestName0"), "first product has incorrect title");
        Assert.isTrue(firstProduct.getPrice()==4000, "first product has incorrect price");
        Assert.isTrue(firstProduct.getDescription().equals("TestDescription0"), "first product has incorrect description");
        Assert.isTrue(firstProduct.getId().equals("65014"),"first product has incorrect id");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(0).equals("Clothing"),"first product has incorrect category");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(1).equals("Mens"),"first product has incorrect category");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(2).equals("Retro Shirts & Jackets"),"first product has incorrect category");
        
        Product secondProduct = products.get(1);
        Assert.isTrue(secondProduct.getName().equals("LFC Mens White Heritage Track Top"), "second product has incorrect title");
        Assert.isTrue(secondProduct.getPrice()==1001, "second product has incorrect price");
        Assert.isTrue(secondProduct.getDescription().equals("Look stylish in this retro white Mens Heritage Track Top."),
                "second product has incorrect description");
        Assert.isTrue(secondProduct.getId().equals("27014"),"second product has incorrect id");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(0).equals("Souvenirs"),"second product has incorrect category");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(1).equals("General"),"second product has incorrect category");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(2).equals("Badges & Keyrings"),"second product has incorrect category");
    }

    @Test
    public void testXMLParseMappings(){

        StringBuilder testBuilder = new StringBuilder();

        testBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        testBuilder.append("<products>");
        testBuilder.append("<product>");
        testBuilder.append("<productName>TestName0</productName>");
        testBuilder.append("<productSku>65014</productSku>");
        testBuilder.append("<productDescription>TestDescription0</productDescription>");
        testBuilder.append("<productPrice>40.00</productPrice>");
        testBuilder.append("<productCategory>Clothing &gt; Mens &gt; Retro Shirts &amp; Jackets</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; Accessories &gt; Clothing &gt; Tops</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("<product>");
        testBuilder.append("<productName>LFC Mens White Heritage Track Top</productName>");
        testBuilder.append("<productSku>27014</productSku>");
        testBuilder.append("<productDescription>Look stylish in this retro white Mens Heritage Track Top.</productDescription>");
        testBuilder.append("<productPrice>10.01</productPrice>");
        testBuilder.append("<productCategory>Souvenirs &gt; General &gt; Badges &amp; Keyrings</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; Accessories</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("</products>");

        String testXML = testBuilder.toString();

        List<Mapping> mappings = XMLParser.parseMapping(testXML);

        Assert.isTrue(mappings.size() == 2,"mappings list of incorrect length");

        Mapping firstMapping = mappings.get(0);
        Product firstProduct = firstMapping.getProduct();
        Assert.isTrue(firstProduct.getName().equals("TestName0"), "first product has incorrect title");
        Assert.isTrue(firstProduct.getPrice()==4000, "first product has incorrect price");
        Assert.isTrue(firstProduct.getDescription().equals("TestDescription0"), "first product has incorrect description");
        Assert.isTrue(firstProduct.getId().equals("65014"),"first product has incorrect id");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(0).equals("Clothing"),"first product has incorrect category");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(1).equals("Mens"),"first product has incorrect category");
        Assert.isTrue(firstProduct.getOriginalCategory().getPart(2).equals("Retro Shirts & Jackets"),"first product has incorrect category");
        Assert.isTrue(firstMapping.getCategory().getPart(0).equals("Apparel & Accessories"),"first mapping category incorrect");
        Assert.isTrue(firstMapping.getCategory().getPart(1).equals("Clothing"),"first mapping category incorrect");
        Assert.isTrue(firstMapping.getCategory().getPart(2).equals("Tops"),"first mapping category incorrect");

        Mapping secondMapping = mappings.get(1);
        Product secondProduct = secondMapping.getProduct();
        Assert.isTrue(secondProduct.getName().equals("LFC Mens White Heritage Track Top"), "second product has incorrect title");
        Assert.isTrue(secondProduct.getPrice()==1001, "second product has incorrect price");
        Assert.isTrue(secondProduct.getDescription().equals("Look stylish in this retro white Mens Heritage Track Top."),
                "second product has incorrect description");
        Assert.isTrue(secondProduct.getId().equals("27014"),"second product has incorrect id");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(0).equals("Souvenirs"),"second product has incorrect category");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(1).equals("General"),"second product has incorrect category");
        Assert.isTrue(secondProduct.getOriginalCategory().getPart(2).equals("Badges & Keyrings"),"second product has incorrect category");
        Assert.isTrue(secondMapping.getCategory().getPart(0).equals("Apparel & Accessories"),"second mapping category incorrect");
    }


}

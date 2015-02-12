package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.junit.Test;
import org.junit.Assert;
import uk.ac.cam.cl.retailcategorymapper.entities.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class XMLTest {

    public void checkCategory(Category c){
        Assert.assertNotNull(c.getAllParts());
        for(String s:c.getAllParts()){
            Assert.assertNotEquals("Empty string in category path",s,"");
        }
    }

    private String generateRandString(int length,Random random){

        StringBuilder answerBuild = new StringBuilder();

        for(int i=0;i<length;i++){
            int t = random.nextInt(26);
            answerBuild.append((char) (t+97));
        }
        return answerBuild.toString();
    }

    @Test
    public void testProductListXMLOutput(){

        Random randGen = new Random(System.currentTimeMillis());

        List<Product> products = new LinkedList<>();

        for(int i=0;i<100;i++) {
            ProductBuilder productBuild = new ProductBuilder();
            productBuild.setName(generateRandString(10, randGen));
            productBuild.setDescription(generateRandString(20, randGen));
            productBuild.setId(generateRandString(10, randGen));
            productBuild.setPrice(randGen.nextInt(10000));
            CategoryBuilder catBuild = new CategoryBuilder();
            catBuild.setId(generateRandString(10, randGen));
            String[] s = new String[]{
                    generateRandString(10, randGen),
                    generateRandString(10, randGen),
                    generateRandString(10, randGen)};
            catBuild.setParts(s);
            productBuild.setOriginalCategory(catBuild.createCategory());
            products.add(productBuild.createProduct());
        }

        String XMLout = XMLParser.productListToXML(products);

        System.out.println(XMLout);

        List<Product> testList = XMLParser.parseProductList(XMLout);

        for(int i=0;i<100;i++){
            Assert.assertEquals("product "+i+" didn't match",products.get(i),testList.get(i));
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
        testBuilder.append("<productPrice>78.45</productPrice>");
        testBuilder.append("<productCategory>Clothing &gt; Mens &gt; RS &amp; J</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; A &gt; Clothing &gt; Tops</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("</products>");

        String testXML = testBuilder.toString();

        List<Product> products = XMLParser.parseProductList(testXML);

        Assert.assertEquals("product list of incorrect length",1,products.size());

        Product firstProduct = products.get(0);
        checkCategory(firstProduct.getOriginalCategory());
        Assert.assertEquals("product has incorrect title", "TestName0", firstProduct.getName());
        Assert.assertEquals("product has incorrect price",7845,(int) firstProduct.getPrice());
        Assert.assertEquals("product has wrong description","TestDescription0",firstProduct.getDescription());
        Assert.assertEquals("product has incorrect id","65014",firstProduct.getId());
        Assert.assertEquals("product has wrong category","Clothing",firstProduct.getOriginalCategory().getPart(0));
        Assert.assertEquals("product has wrong category","Mens",firstProduct.getOriginalCategory().getPart(1));
        Assert.assertEquals("product has wrong category","RS & J",firstProduct.getOriginalCategory().getPart(2));
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
        testBuilder.append("<productPrice>78.99</productPrice>");
        testBuilder.append("<productCategory>Clothing &gt; Mens &gt; RS &amp; J</productCategory>");
        testBuilder.append("<productGoogleCategory>Apparel &amp; A &gt; Clothing &gt; Tops</productGoogleCategory>");
        testBuilder.append("</product>");
        testBuilder.append("</products>");

        String testXML = testBuilder.toString();

        List<Mapping> mappings = XMLParser.parseMapping(testXML);

        Assert.assertEquals("mapping list of incorrect length",1,mappings.size());

        Product firstProduct = mappings.get(0).getProduct();
        checkCategory(firstProduct.getOriginalCategory());
        Assert.assertEquals("product has incorrect title", "TestName0", firstProduct.getName());
        Assert.assertEquals("product has incorrect price",7899,(int) firstProduct.getPrice());
        Assert.assertEquals("product has wrong description","TestDescription0",firstProduct.getDescription());
        Assert.assertEquals("product has incorrect id","65014",firstProduct.getId());
        Assert.assertEquals("product has wrong category","Clothing",firstProduct.getOriginalCategory().getPart(0));
        Assert.assertEquals("product has wrong category","Mens",firstProduct.getOriginalCategory().getPart(1));
        Assert.assertEquals("product has wrong category","RS & J",firstProduct.getOriginalCategory().getPart(2));

        Category cat = mappings.get(0).getCategory();

        Assert.assertEquals("mapping category incorrect","Apparel & A",cat.getPart(0));
        Assert.assertEquals("mapping category incorrect","Clothing",cat.getPart(1));
        Assert.assertEquals("mapping category incorrect","Tops",cat.getPart(2));

   }


}

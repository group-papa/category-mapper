package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class XmlUnmarshallingTest {
    @Test
    public void testProductListXMLOutput() {
        Random randGen = new Random(System.currentTimeMillis());

        List<Product> products = new LinkedList<>();

        for (int i = 0; i < 100; i++) {
            ProductBuilder productBuild = new ProductBuilder();
            productBuild.setName(RandomStringUtils.randomAlphanumeric(10));
            productBuild.setDescription(RandomStringUtils.randomAlphanumeric(20));
            productBuild.setId(RandomStringUtils.randomAlphanumeric(10));
            productBuild.setPrice(randGen.nextInt(10000));
            CategoryBuilder catBuild = new CategoryBuilder();
            catBuild.setId(RandomStringUtils.randomAlphanumeric(10));
            String[] s = new String[]{
                    RandomStringUtils.randomAlphanumeric(10),
                    RandomStringUtils.randomAlphanumeric(10),
                    RandomStringUtils.randomAlphanumeric(10)};
            catBuild.setParts(s);
            productBuild.setOriginalCategory(catBuild.createCategory());

            products.add(productBuild.createProduct());
        }

        String XMLout = new ProductXmlMarshaller().marshal(products);

        List<Product> testList = new XmlProductUnmarshaller().unmarshal(XMLout);

        for (int i = 0; i < 100; i++) {
            Assert.assertEquals("product " + i + " didn't match", products.get(i), testList.get(i));
        }
    }

    @Test
    public void testXMLParseProductList() {
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

        List<Product> products = new XmlProductUnmarshaller().unmarshal(testXML);

        Assert.assertEquals("product list of incorrect length", 1, products.size());

        Product firstProduct = products.get(0);
        checkCategory(firstProduct.getOriginalCategory());
        Assert.assertEquals("product has incorrect title", "TestName0", firstProduct.getName());
        Assert.assertEquals("product has incorrect price", 7845, (int) firstProduct.getPrice());
        Assert.assertEquals("product has wrong description", "TestDescription0", firstProduct.getDescription());
        Assert.assertEquals("product has incorrect id", "65014", firstProduct.getId());
        Assert.assertEquals("product has wrong category", "Clothing", firstProduct.getOriginalCategory().getPart(0));
        Assert.assertEquals("product has wrong category", "Mens", firstProduct.getOriginalCategory().getPart(1));
        Assert.assertEquals("product has wrong category", "RS & J", firstProduct.getOriginalCategory().getPart(2));
    }

    @Test
    public void testXMLParseMappings() {
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

        List<Mapping> mappings = new XmlMappingUnmarshaller().unmarshal(testXML);

        Assert.assertEquals("mapping list of incorrect length", 1, mappings.size());

        Product firstProduct = mappings.get(0).getProduct();
        checkCategory(firstProduct.getOriginalCategory());
        Assert.assertEquals("product has incorrect title", "TestName0", firstProduct.getName());
        Assert.assertEquals("product has incorrect price", 7899, (int) firstProduct.getPrice());
        Assert.assertEquals("product has wrong description", "TestDescription0", firstProduct.getDescription());
        Assert.assertEquals("product has incorrect id", "65014", firstProduct.getId());
        Assert.assertEquals("product has wrong category", "Clothing", firstProduct.getOriginalCategory().getPart(0));
        Assert.assertEquals("product has wrong category", "Mens", firstProduct.getOriginalCategory().getPart(1));
        Assert.assertEquals("product has wrong category", "RS & J", firstProduct.getOriginalCategory().getPart(2));

        Category cat = mappings.get(0).getCategory();

        Assert.assertEquals("mapping category incorrect", "Apparel & A", cat.getPart(0));
        Assert.assertEquals("mapping category incorrect", "Clothing", cat.getPart(1));
        Assert.assertEquals("mapping category incorrect", "Tops", cat.getPart(2));
    }

    private void checkCategory(Category category) {
        Assert.assertNotNull(category.getAllParts());
        for (String part : category.getAllParts()) {
            Assert.assertNotEquals("Empty string in category path", part, "");
        }
    }
}

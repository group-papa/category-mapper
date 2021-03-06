package uk.ac.cam.cl.retailcategorymapper.entities;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the Product entity class.
 */
public class ProductTest {
    private static final String ID = "PRODUCT_ID_123";
    private static final String NAME = "PRODUCT_NAME_ABC";
    private static final String DESCRIPTION = "PRODUCT_DESCRIPTION_ABCDEF";
    private static final Integer PRICE = 123;
    private static final Category CATEGORY = new CategoryBuilder().setParts
            (new String[] {"A", "B", "C"}).createCategory();

    private static final String ATTR_KEY = "ATTRIBUTE_NAME";
    private static final String ATTR_VALUE = "ATTRIBUTE_VALUE";

    @Test
    public void testProductStoresCorrectValues() {
        Product product = new ProductBuilder()
                .setId(ID)
                .setName(NAME)
                .setDescription(DESCRIPTION)
                .setPrice(PRICE)
                .setOriginalCategory(CATEGORY)
                .createProduct();

        Assert.assertEquals(ID, product.getId());
        Assert.assertEquals(NAME, product.getName());
        Assert.assertEquals(DESCRIPTION, product.getDescription());
        Assert.assertEquals(PRICE, product.getPrice());
        Assert.assertEquals(CATEGORY, product.getOriginalCategory());
    }

    @Test
    public void testProductStoresAttributesCorrectly() {
        Product product = new ProductBuilder()
                .addAttribute(ATTR_KEY, ATTR_VALUE)
                .createProduct();

        Assert.assertEquals(ATTR_VALUE, product.getAttribute(ATTR_KEY));
    }
}

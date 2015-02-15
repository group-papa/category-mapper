package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;

/**
 * Test the Category Unmarshaller.
 */
public class CategoryUnmarshallerTest {
    public static final String INPUT_1 = " Cat A >  Cat B >  Cat C  ";
    public static final Category OUTPUT_1 = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat B", "Cat C"}).createCategory();

    public static final String INPUT_2 = " > Cat A >    >  Cat C  >  ";
    public static final Category OUTPUT_2 = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat C"}).createCategory();

    @Test
    public void testCategoryUnmarshaller() {
        CategoryUnmarshaller unmarshaller = new CategoryUnmarshaller();
        Category category = unmarshaller.unmarshal(INPUT_1);

        Assert.assertNotNull(category);
        Assert.assertEquals(OUTPUT_1, category);
    }

    @Test
    public void testCategoryUnmarshallerWithEmptyCategory() {
        CategoryUnmarshaller unmarshaller = new CategoryUnmarshaller();
        Category category = unmarshaller.unmarshal("  ");

        Assert.assertNull(category);
    }

    @Test
    public void testCategoryUnmarshallerWithNewLines() {
        CategoryUnmarshaller unmarshaller = new CategoryUnmarshaller();
        Category category = unmarshaller.unmarshal(" \n \r\n ");

        Assert.assertNull(category);
    }

    @Test
    public void testCategoryUnmarshallerWithEmptySubCategory() {
        CategoryUnmarshaller unmarshaller = new CategoryUnmarshaller();
        Category category = unmarshaller.unmarshal(INPUT_2);

        Assert.assertNotNull(category);
        Assert.assertEquals(OUTPUT_2, category);
    }
}

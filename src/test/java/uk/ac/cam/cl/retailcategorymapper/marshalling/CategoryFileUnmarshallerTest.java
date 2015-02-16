package uk.ac.cam.cl.retailcategorymapper.marshalling;

import org.junit.Assert;
import org.junit.Test;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;

import java.util.List;

/**
 * Test the Category File Unmarshaller.
 */
public class CategoryFileUnmarshallerTest {
    public static final String INPUT = " Cat A >  Cat B >  Cat C  \n Cat A > " +
            " Cat B >  Cat C ";
    public static final Category OUTPUT = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat B", "Cat C"}).createCategory();

    @Test
    public void testCategoryFileUnmarshaller() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal(INPUT);

        Assert.assertEquals(2, categories.size());
        Assert.assertEquals(OUTPUT, categories.get(0));
        Assert.assertEquals(OUTPUT, categories.get(1));
    }

    @Test
    public void testCategoryFileUnmarshallerWithEmptyFile() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal("  \r\n ");

        Assert.assertEquals(0, categories.size());
    }
}

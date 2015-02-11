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
    public static final String INPUT_1 = " Cat A >  Cat B >  Cat C  ";
    public static final Category OUTPUT_1 = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat B", "Cat C"}).createCategory();

    public static final String INPUT_2 = " > Cat A >    >  Cat C  >  ";
    public static final Category OUTPUT_2 = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat C"}).createCategory();

    @Test
    public void testCategoryFileUnmarshaller() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal(INPUT_1);

        Assert.assertEquals(1, categories.size());
        Category category = categories.get(0);
        Assert.assertEquals(OUTPUT_1, category);
    }

    @Test
    public void testCategoryFileUnmarshallerWithEmptyCategory() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal("  ");

        Assert.assertEquals(0, categories.size());
    }

    @Test
    public void testCategoryFileUnmarshallerWithEmptySubCategory() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal(INPUT_2);

        Assert.assertEquals(1, categories.size());
        Category category = categories.get(0);
        Assert.assertEquals(OUTPUT_2, category);
    }
}

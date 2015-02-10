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
    public static final String INPUT = " Cat A >  Cat B >  Cat C  ";
    public static final Category OUTPUT = new CategoryBuilder().setParts(new
            String[] {"Cat A", "Cat B", "Cat C"}).createCategory();

    @Test
    public void testCategoryFileUnmarshaller() {
        CategoryFileUnmarshaller unmarshaller = new CategoryFileUnmarshaller();
        List<Category> categories = unmarshaller.unmarshal(INPUT);

        Assert.assertEquals(1, categories.size());
        Category category = categories.get(0);
        Assert.assertEquals(OUTPUT, category);
    }
}

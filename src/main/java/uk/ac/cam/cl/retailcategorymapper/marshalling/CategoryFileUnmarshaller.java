package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Take a category file and unmarshal it into a list of categories.
 */
public class CategoryFileUnmarshaller implements Unmarshaller<String,
        List<Category>> {
    @Override
    public List<Category> unmarshal(String data) {
        List<Category> categories = new ArrayList<>();
        CategoryUnmarshaller unmarshaller = new CategoryUnmarshaller();

        String[] lines = data.split("[\\r?\\n]+");

        for (String line : lines) {
            Category category = unmarshaller.unmarshal(line);

            if (category != null) {
                categories.add(category);
            }
        }

        return categories;
    }
}

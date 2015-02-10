package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.config.ParsingConfig;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Take a category file and unmarshal it into a list of categories.
 */
public class CategoryFileUnmarshaller implements Unmarshaller<Category> {
    @Override
    public List<Category> unmarshal(String data) {
        List<Category> categories = new ArrayList<>();

        String[] lines = data.split("[\\r?\\n]+");

        for (String line : lines) {
            String[] parts = line.trim().split("\\s*" + ParsingConfig
                    .CATEGORYFILEDELIMITER + "\\s*");
            Category category = new CategoryBuilder().setParts(parts)
                    .createCategory();
            categories.add(category);
        }

        return categories;
    }
}

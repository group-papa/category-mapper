package uk.ac.cam.cl.retailcategorymapper.marshalling;

import uk.ac.cam.cl.retailcategorymapper.config.ParsingConfig;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Take a category file and unmarshal it into a list of categories.
 */
public class CategoryFileUnmarshaller implements Unmarshaller<Category> {
    @Override
    public List<Category> unmarshal(String data) {
        List<Category> categories = new ArrayList<>();

        String[] lines = data.split("[\\r?\\n]+");

        String delimiter = "\\s*" + ParsingConfig.CATEGORY_FILE_DELIMITER +
                "\\s*";
        for (String line : lines) {
            String[] partsArray = line.trim().split(delimiter);
            List<String> parts = new ArrayList<>(Arrays.asList(partsArray));

            parts.removeAll(Collections.singleton(""));
            if (parts.size() == 0) {
                continue;
            }

            Category category = new CategoryBuilder()
                    .setId(Uuid.generateUUID())
                    .setParts(parts.toArray(new String[] {}))
                    .createCategory();
            categories.add(category);
        }

        return categories;
    }
}

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
 * Take a category line and unmarshal it into a category.
 */
public class CategoryUnmarshaller implements Unmarshaller<String, Category> {
    @Override
    public Category unmarshal(String data) {
        if (data.startsWith("#")) {
            return null;
        }

        String delimiter = "\\s*" + ParsingConfig.CATEGORY_FILE_DELIMITER +
                "\\s*";
        String[] partsArray = data.replaceAll("[\\r|\\n]", "")
                .trim().split(delimiter);
        List<String> parts = new ArrayList<>(Arrays.asList(partsArray));

        parts.removeAll(Collections.singleton(""));
        if (parts.size() == 0) {
            return null;
        }

        return new CategoryBuilder()
                .setId(Uuid.generateUUID())
                .setParts(parts.toArray(new String[] {}))
                .createCategory();
    }
}

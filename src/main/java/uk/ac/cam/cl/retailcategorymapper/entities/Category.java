package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.StringUtils;
import uk.ac.cam.cl.retailcategorymapper.config.ParsingConfig;

import java.util.Arrays;

/**
 * An immutable class to store a details about a category.
 */
@JsonDeserialize(builder = CategoryBuilder.class)
public class Category {
    private String id;
    private String[] parts;

    protected Category(String id, String[] parts) {
        this.id = id;

        if (parts.length == 0) {
            throw new IllegalArgumentException();
        }
        this.parts = Arrays.copyOf(parts, parts.length);
    }

    public String getId() {
        return id;
    }

    public int getDepth(){
        return parts.length;
    }

    public String getPart(int n) {
        return parts[n];
    }

    public String[] getAllParts() {
        return Arrays.copyOf(parts, parts.length);
    }

    public String toString() {
        return StringUtils.join(parts, " " + ParsingConfig
                .CATEGORY_FILE_DELIMITER + " ");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Category)) return false;

        Category category = (Category) o;

        if (!Arrays.equals(parts, category.parts)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(parts);
    }
}

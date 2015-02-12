package uk.ac.cam.cl.retailcategorymapper.api.formdata;

import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.util.Streams;

import java.io.IOException;

/**
 * A multipart/form-data field.
 */
public class FormDataField {
    private String fieldName;
    private String name;
    private boolean formField;
    private String contents;

    public FormDataField(FileItemStream item) {
        this.fieldName = item.getFieldName();
        this.name = item.getName();
        this.formField = item.isFormField();
        try {
            this.contents = Streams.asString(item.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getName() {
        return name;
    }

    public boolean isFormField() {
        return formField;
    }

    public String getContents() {
        return contents;
    }
}

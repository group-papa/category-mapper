package uk.ac.cam.cl.retailcategorymapper.api.formdata;

import org.apache.commons.fileupload.util.Streams;

import java.io.IOException;
import java.io.InputStream;

/**
 * A multipart/form-data field.
 */
public class FormDataField {
    private String fieldName;
    private String name;
    private boolean formField;
    private InputStream stream;

    public FormDataField(String fieldName, String name, boolean formField,
                         InputStream stream) {
        this.fieldName = fieldName;
        this.name = name;
        this.formField = formField;
        this.stream = stream;
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

    public InputStream getStream() {
        return stream;
    }

    public String getStreamAsString() {
        try {
            return Streams.asString(stream);
        } catch (IOException e) {
            return null;
        }
    }
}

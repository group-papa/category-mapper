package uk.ac.cam.cl.retailcategorymapper.api;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse requests encoded as multipart/form-data.
 */
public class FormDataParser {
    private Map<String, String> fieldData = new HashMap<>();

    public FormDataParser(HttpServletRequest request) {
        ServletFileUpload servletFileUpload = new ServletFileUpload();
        FileItemIterator fileItemIterator;

        try {
            fileItemIterator = servletFileUpload.getItemIterator(request);

            while (fileItemIterator.hasNext()) {
                FileItemStream item = fileItemIterator.next();
                String name = item.getFieldName();
                fieldData.put(name, Streams.asString(item.openStream()));
            }
        } catch (FileUploadException | IOException e) {
            e.printStackTrace();
        }
    }

    public String getField(String name) {
        return fieldData.get(name);
    }
}

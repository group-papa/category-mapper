package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * An immutable class to store an upload.
 */
@JsonDeserialize(builder = UploadBuilder.class)
public class Upload {
    private String id;
    private String filename;
    private String dateCreated;
    private int productCount;
    private int mappingCount;

    protected Upload(String id, String filename, String dateCreated, int
            productCount, int mappingCount) {
        this.id = id;
        this.filename = filename;
        this.dateCreated = dateCreated;
        this.productCount = productCount;
        this.mappingCount = mappingCount;
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getMappingCount() {
        return mappingCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Upload)) return false;

        Upload upload = (Upload) o;

        if (mappingCount != upload.mappingCount) return false;
        if (productCount != upload.productCount) return false;
        if (!dateCreated.equals(upload.dateCreated)) return false;
        if (!filename.equals(upload.filename)) return false;
        if (!id.equals(upload.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + filename.hashCode();
        result = 31 * result + dateCreated.hashCode();
        result = 31 * result + productCount;
        result = 31 * result + mappingCount;
        return result;
    }
}

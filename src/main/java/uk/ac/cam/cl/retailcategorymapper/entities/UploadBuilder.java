package uk.ac.cam.cl.retailcategorymapper.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

/**
 * A builder class which produces an immutable Upload.
 */
@JsonPOJOBuilder(buildMethodName = "createUpload")
public class UploadBuilder {
    private String id;
    private String filename;
    private String dateCreated;
    private int productCount;
    private int mappingCount;

    @JsonProperty("id")
    public UploadBuilder setId(String id) {
        this.id = id;
        return this;
    }

    @JsonProperty("filename")
    public UploadBuilder setFilename(String filename) {
        this.filename = filename;
        return this;
    }

    @JsonProperty("dateCreated")
    public UploadBuilder setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
        return this;
    }

    @JsonProperty("productCount")
    public UploadBuilder setProductCount(int productCount) {
        this.productCount = productCount;
        return this;
    }

    @JsonProperty("mappingCount")
    public UploadBuilder setMappingCount(int mappingCount) {
        this.mappingCount = mappingCount;
        return this;
    }

    public Upload createUpload() {
        return new Upload(id, filename, dateCreated, productCount,
                mappingCount);
    }
}

package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * An abstract class to store a details about a response.
 */
public abstract class Response {
    private Taxonomy taxonomy;

    public Response(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }
}

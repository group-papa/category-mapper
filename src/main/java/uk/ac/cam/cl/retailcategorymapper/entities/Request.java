package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * An abstract class to store a details about a request.
 */
public abstract class Request {
    private final Taxonomy taxonomy;

    public Request(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    public Taxonomy getTaxonomy() {
        return taxonomy;
    }
}

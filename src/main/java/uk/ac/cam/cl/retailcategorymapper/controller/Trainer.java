package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

/**
 * An abstract class for trainers.
 */
public abstract class Trainer {
    /**
     * The taxonomy this trainer is for.
     */
    private Taxonomy taxonomy;

    /**
     * Construct a new trainer for a given taxonomy.
     * @param taxonomy The taxonomy.
     */
    public Trainer(Taxonomy taxonomy) {
        this.taxonomy = taxonomy;
    }

    /**
     * Get the taxonomy this trainer is for.
     * @return The taxonomy.
     */
    public Taxonomy getTaxonomy() {
        return taxonomy;
    }

    /**
     * Use the given mapping to train with.
     * @param mapping The mapping to train on.
     * @return Whether the mapping was used.
     */
    public abstract boolean train(Mapping mapping);

    /**
     * Save the training data.
     */
    public abstract void save();
}

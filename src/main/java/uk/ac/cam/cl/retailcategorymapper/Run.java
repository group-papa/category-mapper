package uk.ac.cam.cl.retailcategorymapper;

import uk.ac.cam.cl.retailcategorymapper.api.Router;
import uk.ac.cam.cl.retailcategorymapper.db.InitialiseDb;

/**
 * Main method which runs the backend.
 */
public class Run {
    public static void main(String[] args) {
        // Initialise
        InitialiseDb.initialise();

        // Run the router
        Router.run();
    }
}

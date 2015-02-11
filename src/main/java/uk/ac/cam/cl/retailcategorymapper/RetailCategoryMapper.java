package uk.ac.cam.cl.retailcategorymapper;

import uk.ac.cam.cl.retailcategorymapper.api.Router;
import uk.ac.cam.cl.retailcategorymapper.db.InitialiseDb;

/**
 * Main method which runs the backend.
 */
public class RetailCategoryMapper {
    public static void main(String[] args) {
        // Initialise
        InitialiseDb.initialise();

        // Run the router
        Router.run();
    }
}

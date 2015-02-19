package uk.ac.cam.cl.retailcategorymapper.manual;

import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.db.ManualMappingDb;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A trainer implementation for manual mappings.
 */
public class ManualTrainer extends Trainer {
    private Set<Category> taxonomyCategories;
    private Map<String, Category> newManualMappings;

    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public ManualTrainer(Taxonomy taxonomy) {
        super(taxonomy);
        taxonomyCategories = new HashSet<>(
                TaxonomyDb.getCategoriesForTaxonomy(taxonomy));
        newManualMappings = new HashMap<>();
    }

    /**
     * Save the given mapping as a manual mapping.
     * @param mapping The mapping.
     * @return Whether the mapping was used.
     */
    @Override
    public boolean train(Mapping mapping) {
        Taxonomy taxonomy = getTaxonomy();
        Product product = mapping.getProduct();
        Category category = mapping.getCategory();

        if (taxonomy == null || product == null || category == null) {
            return false;
        }

        if (!taxonomyCategories.contains(category)) {
            return false;
        }

        newManualMappings.put(product.getId(), category);
        return true;
    }

    /**
     * Save the manual mappings.
     */
    @Override
    public void save() {
        ManualMappingDb.getManualMappings(getTaxonomy())
                .putAll(newManualMappings);
        newManualMappings = new HashMap<>();
    }
}

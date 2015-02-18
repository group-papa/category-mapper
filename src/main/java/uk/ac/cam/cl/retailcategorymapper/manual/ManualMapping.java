package uk.ac.cam.cl.retailcategorymapper.manual;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.db.ManualMappingDb;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A classifier implementation for manual mappings.
 */
public class ManualMapping extends Classifier {
    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public ManualMapping(Taxonomy taxonomy) {
        super(taxonomy);
    }

    @Override
    public List<Mapping> classify(Product product) {
        Taxonomy taxonomy = getTaxonomy();

        MappingBuilder mappingBuilder = new MappingBuilder()
                .setTaxonomy(taxonomy)
                .setProduct(product)
                .setMethod(Method.MANUAL)
                .setConfidence(0);

        Map<String, Category> manualMappings = ManualMappingDb
                .getManualMappings(taxonomy);

        if (manualMappings.containsKey(product.getId())) {
            mappingBuilder
                    .setCategory(manualMappings.get(product.getId()))
                    .setConfidence(1);
        }

        return Collections.singletonList(mappingBuilder.createMapping());
    }

    @Override
    public boolean train(Mapping mapping) {
        Taxonomy taxonomy = getTaxonomy();
        Product product = mapping.getProduct();
        Category category = mapping.getCategory();

        if (taxonomy == null || product == null || category == null) {
            return false;
        }

        Set<Category> taxonomyCategories = TaxonomyDb
                .getCategoriesForTaxonomy(taxonomy);

        if (!taxonomyCategories.contains(category)) {
            return false;
        }

        Map<String, Category> manualMappings = ManualMappingDb
                .getManualMappings(taxonomy);
        manualMappings.put(product.getId(), category);

        return true;
    }
}

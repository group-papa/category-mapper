package uk.ac.cam.cl.retailcategorymapper.manual;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.db.ManualMappingDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A classifier implementation for manual mappings.
 */
public class ManualClassifier extends Classifier {
    private Map<String, Category> manualMappings;

    /**
     * Construct a new classifier for a given taxonomy.
     *
     * @param taxonomy The taxonomy.
     */
    public ManualClassifier(Taxonomy taxonomy) {
        super(taxonomy);
        manualMappings = new HashMap<>(
                ManualMappingDb.getManualMappings(taxonomy));
    }

    /**
     * Classify the given product using manual mappings.
     * @param product The product.
     * @return A list containing a single manual mapping.
     */
    @Override
    public List<Mapping> classify(Product product) {
        Taxonomy taxonomy = getTaxonomy();

        MappingBuilder mappingBuilder = new MappingBuilder()
                .setTaxonomy(taxonomy)
                .setProduct(product)
                .setMethod(Method.MANUAL)
                .setConfidence(0);

        if (manualMappings.containsKey(product.getId())) {
            mappingBuilder
                    .setCategory(manualMappings.get(product.getId()))
                    .setConfidence(Float.MAX_VALUE);
        }

        return Collections.singletonList(mappingBuilder.createMapping());
    }
}

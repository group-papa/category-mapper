package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

public class RecursiveBayesTrainer extends Trainer {

    private NaiveBayesStorage storage;
    private Map<Category, Taxonomy> subTaxonomyMap;
    int level;

    public RecursiveBayesTrainer(Taxonomy taxonomy, NaiveBayesStorage storage, int level) {
        super(taxonomy);
        this.storage = storage;
        this.level = level;
        this.subTaxonomyMap = storage.getCategorySubTaxonomyMap(this.getTaxonomy());
    }

    public RecursiveBayesTrainer(Taxonomy taxonomy) {
        this(taxonomy, NaiveBayesDb.getInstance(), 0);
    }

    public RecursiveBayesTrainer(Taxonomy taxonomy, NaiveBayesStorage storage) {
        this(taxonomy, storage, 0);
    }

    private Category getSingleLevelCategory(Category category) {
        return new CategoryBuilder().setId(UUID.randomUUID().toString())
                .setParts(Arrays.copyOfRange(category.getAllParts(), 0, this.level + 1))
                .createCategory();
    }

    private Mapping getSingleLevelMapping(Mapping mapping) {
        return new MappingBuilder().setCategory(getSingleLevelCategory(mapping.getCategory()))
                .setConfidence(mapping.getConfidence()).setMethod(mapping.getMethod())
                .setProduct(mapping.getProduct()).setTaxonomy(this.getTaxonomy()).createMapping();
    }

    @Override
    public boolean train(Mapping mapping) {
        Mapping truncatedMapping = getSingleLevelMapping(mapping);
        if (!this.getTaxonomy().getCategories().contains(truncatedMapping.getCategory())) {
            this.getTaxonomy().getCategories().add(truncatedMapping.getCategory());
        }
        NaiveBayesDbTrainer trainer = new NaiveBayesDbTrainer(this.getTaxonomy(), this.storage);
        trainer.train(truncatedMapping);
        trainer.save();
        if (mapping.getCategory().getDepth() > this.level + 1) {
            if (!this.subTaxonomyMap.containsKey(truncatedMapping.getCategory())) {
                this.subTaxonomyMap.put(truncatedMapping.getCategory(), new TaxonomyBuilder()
                        .setId(this.getTaxonomy().getId() + "__").setName(this.getTaxonomy()
                                .getName() + "__").createNonDbTaxonomy());
            }
            Taxonomy nextLevelTaxonomy = this.subTaxonomyMap.get(truncatedMapping.getCategory());
            RecursiveBayesTrainer nextLevelTrainer = new RecursiveBayesTrainer(nextLevelTaxonomy,
                    this.storage, this.level + 1);
            return nextLevelTrainer.train(mapping);
        } else {
            return true;
        }
    }

    @Override
    public void save() {

    }
}

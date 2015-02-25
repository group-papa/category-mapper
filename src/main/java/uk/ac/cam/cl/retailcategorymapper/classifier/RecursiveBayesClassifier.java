package uk.ac.cam.cl.retailcategorymapper.classifier;

import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class RecursiveBayesClassifier extends Classifier {

    private Map<Category, Taxonomy> subTaxonomyMap;
    private NaiveBayesStorage storage;
    int level;

    public RecursiveBayesClassifier(Taxonomy taxonomy, NaiveBayesStorage storage, int level) {
        super(taxonomy);
        this.storage = storage;
        this.level = level;
        this.subTaxonomyMap = storage.getCategorySubTaxonomyMap(this.getTaxonomy());
    }

    public RecursiveBayesClassifier(Taxonomy taxonomy) {
        this(taxonomy, NaiveBayesDb.getInstance(), 0);
    }

    public RecursiveBayesClassifier(Taxonomy taxonomy, NaiveBayesStorage storage) {
        this(taxonomy, storage, 0);
    }

    private Category getSingleLevelCategory(Category category) {
        return new CategoryBuilder().setId(UUID.randomUUID().toString())
                .setParts(Arrays.copyOfRange(category.getAllParts(), 0, this.level + 1))
                .createCategory();
    }

    @Override
    public List<Mapping> classify(Product product) {
        NaiveBayesDbClassifier classifier = new NaiveBayesDbClassifier(this.getTaxonomy(),
                this.storage);
        List<Mapping> firstMappings = classifier.classify(product);
        Category firstCategory = firstMappings.get(0).getCategory();
        //System.out.println("classified under " + firstCategory.toString());
        if (!this.subTaxonomyMap.containsKey(firstCategory)) {
            return firstMappings;
        } else {
            RecursiveBayesClassifier nextLevelClassifier = new RecursiveBayesClassifier(this
                    .subTaxonomyMap.get(firstCategory), this.storage, this.level + 1);
            return nextLevelClassifier.classify(product);
        }
    }
}

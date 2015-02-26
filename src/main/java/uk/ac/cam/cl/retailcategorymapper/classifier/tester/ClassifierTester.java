package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Charlie
 */
public class ClassifierTester {
    Classifier classifier;
    Trainer trainer;
    List<Mapping> testData;
    Taxonomy taxonomy;
    static final int depthConsidered = 5;

    /* we load the classifier tested with a classifier and a list of mappings */
    public ClassifierTester(Classifier classifier, Trainer trainer, List<Mapping> mappings,
                            Taxonomy taxonomy) {
        this.classifier = classifier;
        this.trainer = trainer;
        this.taxonomy = taxonomy;
        this.testData = new LinkedList<>(mappings);
    }

     public static void main(String[] args) throws IOException {
        System.out.println("ClassifierTest Main Executed");
        System.out.println("Files: " + Arrays.toString(args));
        System.out.println();

        NaiveBayesFakeTestDb storage = NaiveBayesFakeTestDb.getInstance();
        List<Mapping> trainMappings = new ArrayList<>();
        List<Mapping> testMappings = new ArrayList<>();
        Random rand = new Random();
        XmlMappingUnmarshaller unmarshaller = new XmlMappingUnmarshaller();
        Taxonomy taxonomy = new TaxonomyBuilder().setId(UUID.randomUUID().toString()).setName
                ("Test Taxonomy").createNonDbTaxonomy();
        Set<Category> taxonomyCategories = taxonomy.getCategories();

        System.out.println("loading products from files...");


        for (String filename : args) {
            List<Mapping> inputMappings = unmarshaller.unmarshal(new String(Files.readAllBytes
                    (Paths.get(filename)), StandardCharsets.UTF_8));
            //Collections.shuffle(inputProducts);
            //inputProducts = inputProducts.subList(0, 500);
            for (Mapping inputMapping : inputMappings) {
                /*Product p = new ProductBuilder().setName(inputProduct.getName()).setDescription
                        (inputProduct.getDescription()).setId(inputProduct.getId())
                        .setOriginalCategory(inputProduct.getOriginalCategory())
                        .setDestinationCategory(new CategoryBuilder().setId(UUID.randomUUID()
                                .toString()).setParts(new String[] { inputProduct
                                .getDestinationCategory().getPart(0) }).createCategory())
                        .createProduct();*/
                // Split products 80/20 into train/test
                if (rand.nextDouble() > 0.8) {
                    testMappings.add(inputMapping);
                } else {
                    trainMappings.add(inputMapping);
                }
                taxonomyCategories.add(inputMapping.getCategory());
            }
        }

        /*Collections.shuffle(trainProducts);
        Collections.shuffle(testProducts);
        trainProducts = trainProducts.subList(0, 8000);
        testProducts = testProducts.subList(0, 2000);*/

        NaiveBayesDbTrainer trainer = new NaiveBayesDbTrainer(taxonomy, storage);

         int numProductsTrained = 0;

        for (Mapping mapping : trainMappings) {
            if (numProductsTrained % 1000 == 0)
                System.out.format("trained: %d of %d\n", numProductsTrained, trainMappings.size());
            trainer.train(mapping);
            //System.out.println("trained on " + product.getName());
            numProductsTrained++;
        }

        System.out.format("trained: %d of %d\n", numProductsTrained, trainMappings.size());

        trainer.save();

        Classifier classifier = new NaiveBayesDbClassifier(taxonomy, storage);

        int totalProducts = 0;
        int correctProducts = 0;


        //int[] levelCorrectProducts = new int[taxonomyCategories.stream().mapToInt(category ->
        //       category.getDepth()).max().getAsInt()];
        int[] levelCorrectProducts = new int[10];
        Arrays.fill(levelCorrectProducts, 0);
        int[] levelTotalProducts = new int[levelCorrectProducts.length];
        Arrays.fill(levelTotalProducts, 0);

        for (Mapping testMapping : testMappings) {
            if (totalProducts % 1000 == 0)
                System.out.format("classified: %d of %d\n", totalProducts, testMappings.size());
            Mapping classifiedMapping = classifier.classify(testMapping.getProduct()).get(0);
            /*System.out.format("Classified as %s: %s (originally, %s; manually, %s)\n", m
                    .getCategory().toString(), m.getProduct().getName(), m.getProduct()
                    .getOriginalCategory().toString(), m.getProduct().M()
                    .toString());*/
            totalProducts++;
            if (classifiedMapping.getCategory().equals(testMapping.getCategory())) {
                correctProducts++;
            }

            for (int i = 0; i < testMapping.getCategory().getDepth(); i++) {
                levelTotalProducts[i]++;
            }
            int maxDepth = Math.max(classifiedMapping.getCategory().getDepth(), testMapping.getCategory()
                    .getDepth());
            for (int i = 0; i < maxDepth; i++) {
                try {
                    if (classifiedMapping.getCategory().getPart(i).equals(testMapping.getCategory().getPart(i)
		                    )) {
                        levelCorrectProducts[i]++;
                    } else {
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }

        System.out.format("classified: %d of %d\n", totalProducts, testMappings.size());

        System.out.println();
        System.out.format("==== Overall accuracy: %.2f%%\n", ((double) correctProducts / (double)
                totalProducts) * 100.0);
        for (int i = 0; i < levelCorrectProducts.length; i++) {
            System.out.format("%d level accuracy: %.2f%%\n", i + 1, ((double)
                    levelCorrectProducts[i] / (double) levelTotalProducts[i]) * 100.0);
        }
    }
}

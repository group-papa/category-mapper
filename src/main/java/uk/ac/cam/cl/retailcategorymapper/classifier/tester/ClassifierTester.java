package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlProductUnmarshaller;

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

    public double[] test(int iterationsNeeded) {
        Random randGen = new Random(System.currentTimeMillis());

        int[] successes = new int[depthConsidered];
        int[] trials = new int[depthConsidered];

        for (int i = 0; i < depthConsidered; i++) {
            successes[i] = 0;
            trials[i] = 0;
        }

        for (int iterations = 0; iterations < iterationsNeeded; iterations++) {
            List<Mapping> mappingsToDo = new LinkedList<>();

            /*
             * TODO clear all training data from the classifier before training
             * this needs a change to the classifier interface
             */

            //this loop allocates 80% of the data to the test data-set and 20% to be tested
            for (Mapping m : testData) {
                if (randGen.nextInt(5) == 0) {
                    mappingsToDo.add(m);
                } else {
                    trainer.train(m);
                }
            }

            for (Mapping originalMapping : mappingsToDo) {
                Mapping answerMapping = classifier.classify(originalMapping.getProduct()).get(0);

                Category originalCategory = originalMapping.getCategory();
                Category answerCategory = answerMapping.getCategory();

                int minDepth = Math.min(originalCategory.getDepth(), answerCategory.getDepth());
                minDepth = Math.min(depthConsidered, minDepth);

                for (int i = 0; i < minDepth; i++) {
                    trials[i]++;
                    if (originalCategory.getPart(i).equals(answerCategory.getPart(i))) {
                        successes[i]++;
                    }
                }
            }
        }

        double[] accuracyPerLevel = new double[depthConsidered];

        for (int i = 0; i < depthConsidered; i++) {
            if (trials[i] == 0) {
                accuracyPerLevel[i] = 0;
            } else {
                accuracyPerLevel[i] = ((double) successes[i]) / ((double) trials[i]);
            }
        }

        return accuracyPerLevel;
    }

    public static void main(String[] args) throws IOException {
        System.out.println("ClassifierTest Main Executed");
        System.out.println("Files: " + Arrays.toString(args));
        System.out.println();

        NaiveBayesFakeTestDb storage = NaiveBayesFakeTestDb.getInstance();
        List<Product> trainProducts = new ArrayList<Product>();
        List<Product> testProducts = new ArrayList<Product>();
        Random rand = new Random();
        XmlProductUnmarshaller unmarshaller = new XmlProductUnmarshaller();
        Taxonomy taxonomy = new TaxonomyBuilder().setId(UUID.randomUUID().toString()).setName
                ("Test Taxonomy").createNonDbTaxonomy();
        Set<Category> taxonomyCategories = taxonomy.getCategories();

        System.out.println("loading products from files...");

        for (String filename : args) {
            List<Product> inputProducts = unmarshaller.unmarshal(new String(Files.readAllBytes
                    (Paths.get(filename)), StandardCharsets.UTF_8));
            //Collections.shuffle(inputProducts);
            //inputProducts = inputProducts.subList(0, 500);
            for (Product inputProduct : inputProducts) {
                /*Product p = new ProductBuilder().setName(inputProduct.getName()).setDescription
                        (inputProduct.getDescription()).setId(inputProduct.getId())
                        .setOriginalCategory(inputProduct.getOriginalCategory())
                        .setDestinationCategory(new CategoryBuilder().setId(UUID.randomUUID()
                                .toString()).setParts(new String[] { inputProduct
                                .getDestinationCategory().getPart(0) }).createCategory())
                        .createProduct();*/
                Product p = inputProduct;
                // Split products 80/20 into train/test
                if (rand.nextDouble() > 0.05) {
                    testProducts.add(p);
                } else {
                    trainProducts.add(p);
                }
                taxonomyCategories.add(p.getDestinationCategory());
            }
        }

        /*Collections.shuffle(trainProducts);
        Collections.shuffle(testProducts);
        trainProducts = trainProducts.subList(0, 8000);
        testProducts = testProducts.subList(0, 2000);*/

        Trainer trainer = new NaiveBayesDbTrainer(taxonomy, storage);

        int numProductsTrained = 0;

        for (Product product : trainProducts) {
            if (numProductsTrained % 1000 == 0)
                System.out.format("trained: %d of %d\n", numProductsTrained, trainProducts.size());
            Mapping trainingMapping = new MappingBuilder().setMethod(Method.MANUAL).setCategory
                    (product.getDestinationCategory()).setTaxonomy(taxonomy).setProduct(product)
                    .setConfidence(1.0).createMapping();
            trainer.train(trainingMapping);
            //System.out.println("trained on " + product.getName());
            numProductsTrained++;
        }

        System.out.format("trained: %d of %d\n", numProductsTrained, trainProducts.size());

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

        for (Product p : testProducts) {
            if (totalProducts % 1000 == 0)
                System.out.format("classified: %d of %d\n", totalProducts, testProducts.size());
            Mapping m = classifier.classify(p).get(0);
            /*System.out.format("Classified as %s: %s (originally, %s; manually, %s)\n", m
                    .getCategory().toString(), m.getProduct().getName(), m.getProduct()
                    .getOriginalCategory().toString(), m.getProduct().getDestinationCategory()
                    .toString());*/
            totalProducts++;
            if (m.getCategory().equals(p.getDestinationCategory())) {
                correctProducts++;
            }

            for (int i = 0; i < p.getDestinationCategory().getDepth(); i++) {
                levelTotalProducts[i]++;
            }
            int maxDepth = Math.max(m.getCategory().getDepth(), p.getDestinationCategory()
                    .getDepth());
            for (int i = 0; i < maxDepth; i++) {
                try {
                    if (m.getCategory().getPart(i).equals(p.getDestinationCategory().getPart(i))) {
                        levelCorrectProducts[i]++;
                    } else {
                        break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }

        System.out.format("classified: %d of %d\n", totalProducts, testProducts.size());

        System.out.println();
        System.out.format("==== Overall accuracy: %.2f%%\n", ((double) correctProducts / (double)
                totalProducts) * 100.0);
        for (int i = 0; i < levelCorrectProducts.length; i++) {
            System.out.format("%d level accuracy: %.2f%%\n", i + 1, ((double)
                    levelCorrectProducts[i] / (double) levelTotalProducts[i]) * 100.0);
        }
    }
}

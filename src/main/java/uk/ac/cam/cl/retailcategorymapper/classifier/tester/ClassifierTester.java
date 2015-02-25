package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.entities.*;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlProductUnmarshaller;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

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
    public ClassifierTester(Classifier classifier, Trainer trainer,
                            List<Mapping> mappings, Taxonomy taxonomy) {
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
        Taxonomy taxonomy = new TaxonomyBuilder().setId(UUID.randomUUID().toString()).setName("Test Taxonomy").createNonDbTaxonomy();
        Set<Category> taxonomyCategories = taxonomy.getCategories();


        for (String filename : args) {
            List<Product> inputProducts = unmarshaller.unmarshal(new String(Files.readAllBytes(Paths.get(filename)), StandardCharsets.UTF_8));

            for (Product p : inputProducts) {
                // Split products 80/20 into train/test
                if (rand.nextDouble() > 0.8) {
                    testProducts.add(p);
                } else {
                    trainProducts.add(p);
                }
                taxonomyCategories.add(p.getDestinationCategory());
            }
        }

        NaiveBayesDbTrainer trainer = new NaiveBayesDbTrainer(taxonomy, storage);

        for (Product product : trainProducts) {
            Mapping trainingMapping = new MappingBuilder().setMethod(Method.MANUAL).setCategory(product.getDestinationCategory()).setTaxonomy(taxonomy).setProduct(product).setConfidence(1.0).createMapping();
            trainer.train(trainingMapping);
        }

        trainer.save();

        NaiveBayesDbClassifier classifier = new NaiveBayesDbClassifier(taxonomy, storage);

        int totalProducts = 0;
        int correctProducts = 0;

        for (Product p : testProducts) {
            if (totalProducts % 1000 == 0) System.out.println(totalProducts);
            Mapping m = classifier.classify(p).get(0);
            /*System.out.format("Classified as %s: %s (originally, %s; manually, %s)\n", String
                    .join(" > ", m.getCategory().getAllParts()), m.getProduct().getName(), String
                    .join(" > ", m.getProduct().getOriginalCategory().getAllParts()), String.join
                    (" > ", m.getProduct().getDestinationCategory().getAllParts()));*/
            totalProducts++;
            if (m.getCategory().equals(p.getDestinationCategory())) {
                correctProducts++;
            }
        }

        System.out.println(totalProducts);

        System.out.println();
        System.out.format("==== Overall accuracy: %.2f%%\n", ((double) correctProducts / (double)
        totalProducts) * 100.0);
    }
}

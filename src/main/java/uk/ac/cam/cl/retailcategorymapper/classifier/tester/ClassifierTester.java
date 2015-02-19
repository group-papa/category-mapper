package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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

    public static void main(String[] args) {
        try {
            //TODO connect input arguments to the filePath and taxonomy references
            System.out.println("ClassifierTest Main Executed");
            System.out.println(Arrays.toString(args));

            Taxonomy taxonomy = null;
            Classifier classifier = new NaiveBayesDbClassifier(taxonomy);
            Trainer trainer = new NaiveBayesDbTrainer(taxonomy);
            String filePath = "";
            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            StringBuilder inputData = new StringBuilder();
            String s = reader.readLine();
            while (s != null) {
                inputData.append(s);
                s = reader.readLine();
            }
            String inputString = inputData.toString();
            List<Mapping> inputMappings = new XmlMappingUnmarshaller().unmarshal(inputString);

            ClassifierTester tester = new ClassifierTester(classifier,
                    trainer, inputMappings, taxonomy);

            double[] results = tester.test(1);

            for (int i = 0; i < results.length; i++) {
                System.out.println("Level " + i + " accuracy : " + results[i]);
            }
        } catch (FileNotFoundException e) {
            System.err.println("Classifier test failed - file not found exception");
        } catch (IOException e) {
            System.err.println("Classifier test failed - IO exception");
        }
    }
}

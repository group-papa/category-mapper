package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.classifier.features.FeatureConverter2;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Controller;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.db.NaiveBayesDb;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyResponse;
import uk.ac.cam.cl.retailcategorymapper.entities.Feature;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainResponse;
import uk.ac.cam.cl.retailcategorymapper.entities.Upload;
import uk.ac.cam.cl.retailcategorymapper.entities.UploadBuilder;
import uk.ac.cam.cl.retailcategorymapper.marshalling.CategoryFileUnmarshaller;
import uk.ac.cam.cl.retailcategorymapper.marshalling.XmlMappingUnmarshaller;
import uk.ac.cam.cl.retailcategorymapper.utils.DateTime;
import uk.ac.cam.cl.retailcategorymapper.utils.Uuid;

import javax.naming.ldap.Control;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * Created by Charlie
 */
public class ClassifierTester {
    static final int depthConsidered = 5;

    public static double[] test(List<Mapping> mappingsToDo,int iterationsNeeded) {
        Random randGen = new Random(System.currentTimeMillis());
        NaiveBayesClassifier classifier;

        int[] successes = new int[depthConsidered];
        int[] trials = new int[depthConsidered];

        for (int i = 0; i < depthConsidered; i++) {
            successes[i] = 0;
            trials[i] = 0;
        }

        for (int iterations = 0; iterations < iterationsNeeded; iterations++) {

            System.out.println("performing iteration "+(iterations+1)+" of "+iterationsNeeded);

            classifier = new NaiveBayesClassifier();

            Set<Mapping> trainingData = new HashSet<>();
            Set<Mapping> testData = new HashSet<>();
            Set<Category> allCategoriesSeen = new HashSet<>();

            for (Mapping mapping : mappingsToDo) {
                if (randGen.nextInt(5) == 0) {
                    testData.add(mapping);
                } else {
                    trainingData.add(mapping);
                }
            }

            for(Mapping m:trainingData){
                List<Feature> features = FeatureConverter2.changeProductToFeature(m.getProduct());
                for(Feature f:features) {
                    classifier.addSeenFeatureInSpecifiedCategory(f, m.getCategory());
                }
                allCategoriesSeen.add(m.getCategory());
            }

            for(Mapping m:testData) {

                Category originalCategory = m.getCategory();
                Category answerCategory = classifier.classifyWithBagOfWords(m.getProduct(), allCategoriesSeen).getCategory();

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
                System.out.println(i+" "+successes[i]+" "+trials[i]);
                accuracyPerLevel[i] = ((double) successes[i]) / ((double) trials[i]);
            }
        }

        for(int i=0;i<depthConsidered;i++){
            System.out.println(successes[i]);
        }

        return accuracyPerLevel;
    }

    public static void main(String[] args) {
        try {

            FeatureConverter2.addToBlackList("and");
            FeatureConverter2.addToBlackList("or");
            FeatureConverter2.addToBlackList("for");
            FeatureConverter2.addToBlackList("in");
            FeatureConverter2.addToBlackList("as");

            if(args.length!=2){
                throw new RuntimeException("incorrect number of arguements");
            }

            String filePath = args[0];
            String taxonomyPath = args[1];

            System.out.println("ClassifierTest Main Executed");
            System.out.println(Arrays.toString(args));

            BufferedReader reader = new BufferedReader(new FileReader(filePath));

            StringBuilder inputData = new StringBuilder();
            String s = reader.readLine();
            while (s != null) {
                inputData.append(s);
                s = reader.readLine();
            }
            String inputString = inputData.toString();
            List<Mapping> inputMappings = new XmlMappingUnmarshaller().unmarshal(inputString);

            reader = new BufferedReader(new FileReader(taxonomyPath));
            inputData = new StringBuilder();
            s = reader.readLine();
            while (s != null) {
                inputData.append(s);
                inputData.append("\n");
                s = reader.readLine();
            }
            inputString = inputData.toString();
            List<Category> inputCategories = new CategoryFileUnmarshaller().unmarshal(inputString);

            //at this point all the data is loaded

            double[] accuracy = test(inputMappings,10);

            for(int i=0;i<accuracy.length;i++){
                System.out.println(accuracy[i]);
            }

        } catch (FileNotFoundException e) {
            System.err.println("Classifier test failed - file not found exception");
        } catch (IOException e) {
            System.err.println("Classifier test failed - IO exception");
        }
    }
}

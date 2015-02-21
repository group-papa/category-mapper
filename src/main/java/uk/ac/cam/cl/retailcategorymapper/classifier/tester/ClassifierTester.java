package uk.ac.cam.cl.retailcategorymapper.classifier.tester;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbTrainer;
import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.controller.Controller;
import uk.ac.cam.cl.retailcategorymapper.controller.Trainer;
import uk.ac.cam.cl.retailcategorymapper.db.TaxonomyDb;
import uk.ac.cam.cl.retailcategorymapper.db.UploadDb;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainRequest;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Charlie
 */
public class ClassifierTester {
    Classifier classifier;
    List<Mapping> testData;
    static final int depthConsidered = 5;
    Taxonomy taxonomy;

    /* we load the classifier tested with a classifier and a list of mappings */
    public ClassifierTester(Classifier classifier,List<Mapping> mappings,Taxonomy taxonomy) {
        this.classifier = classifier;
        this.testData = new LinkedList<>(mappings);
        this.taxonomy = taxonomy;
    }

    public double[] test(List<Mapping> mappingsToDo,int iterationsNeeded) {
        Random randGen = new Random(System.currentTimeMillis());

        int[] successes = new int[depthConsidered];
        int[] trials = new int[depthConsidered];

        for (int i = 0; i < depthConsidered; i++) {
            successes[i] = 0;
            trials[i] = 0;
        }

        for (int iterations = 0; iterations < iterationsNeeded; iterations++) {

            //for (Mapping originalMapping : mappingsToDo) {
                Mapping originalMapping = mappingsToDo.get(0);
                System.out.println(originalMapping.getProduct().getName());
                List<Mapping> answerMappings = classifier.classify(originalMapping.getProduct());

                if(answerMappings.size()==0){continue;}

                Mapping answerMapping = answerMappings.get(0);

                Category originalCategory = originalMapping.getCategory();
                Category answerCategory = answerMapping.getCategory();


                System.out.println("original   "+originalCategory.toString());
                System.out.println("answer     "+answerCategory.toString());
                System.out.println();

                int minDepth = Math.min(originalCategory.getDepth(), answerCategory.getDepth());
                minDepth = Math.min(depthConsidered, minDepth);

                for (int i = 0; i < minDepth; i++) {
                    trials[i]++;
                    if (originalCategory.getPart(i).equals(answerCategory.getPart(i))) {
                        successes[i]++;
                    }
                }
            //}
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

            if(args.length!=2){
                throw new RuntimeException("incorrect number of arguements");
            }

            String filePath = args[0];
            String taxonomyPath = args[1];

            //TODO connect input arguments to the filePath and taxonomy references
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
            System.out.println(inputCategories.size()+" input categories were found");

            TaxonomyBuilder taxonomyBuilder = new TaxonomyBuilder();
            taxonomyBuilder.setName("test Taxonomy");
            taxonomyBuilder.setId("tax"+System.currentTimeMillis());
            Taxonomy taxonomy = taxonomyBuilder.createTaxonomy();
            TaxonomyDb.setTaxonomy(taxonomy,new HashSet<>(inputCategories));

            Upload upload = new UploadBuilder()
                    .setId(Uuid.generateUUID())
                    .setFilename(filePath)
                    .setDateCreated(DateTime.getCurrentTimeIso8601())
                    .setProductCount(inputMappings.size())
                    .setMappingCount(inputMappings.size())
                    .createUpload();

            Map<String,Product> productMap = new HashMap<>();
            Map<String,Mapping> mappingMap = new HashMap<>();
            for(Mapping m:inputMappings){
                productMap.put(m.getProduct().getId(),m.getProduct());
                mappingMap.put(m.getProduct().getId(), m);
            }

            UploadDb.setUpload(upload,productMap,mappingMap);

            Controller controller = new Controller();

            controller.train(new TrainRequest(taxonomy, inputMappings,false,true));

            Classifier classifier = new NaiveBayesDbClassifier(taxonomy);

            ClassifierTester tester = new ClassifierTester(classifier, inputMappings, taxonomy);




            double[] results = tester.test(inputMappings,1);

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

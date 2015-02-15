package uk.ac.cam.cl.retailcategorymapper.classifier.Tester;

import uk.ac.cam.cl.retailcategorymapper.controller.Classifier;
import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Charlie on 15/02/2015.
 *
 * An alternative implementation would be to split the input mappings set several times
 * randomly and then to run the method several times to get more test data.
 */

public class ClassifierTester {

    Classifier classifier;
    List<Mapping> testData;
    Taxonomy taxonomy;
    static final int depthConsidered = 5;

    /* we load the classifier tested with a classifier and a list of mappings */
    public ClassifierTester(Classifier classifier,List<Mapping> mappings,Taxonomy taxonomy){
        this.classifier=classifier;
        this.taxonomy = taxonomy;
        //we use 20% of the dataset for testing and 80% for training

        List<Mapping> copy = new LinkedList<>(mappings);
        testData = new LinkedList<>();
        int i=0;
        while(!copy.isEmpty()) {
            i++;
            if(i>=5){
                i-=5;
                testData.add(copy.remove(0));
            } else {
                classifier.train(copy.remove(0));
            }
        }
    }

    public double[] Test(){

        int[] successes = new int[depthConsidered];
        int[] trials = new int [depthConsidered];

        for(int i=0;i<depthConsidered;i++){
            successes[i]=0;
            trials[i]=0;
        }

        for(Mapping originalMapping:testData){
            Mapping answerMapping = classifier.classify(taxonomy, originalMapping.getProduct());

            Category originalCategory = originalMapping.getCategory();
            Category answerCategory = answerMapping.getCategory();

            int minDepth = Math.min(originalCategory.getDepth(),answerCategory.getDepth());
            minDepth = Math.min(depthConsidered,minDepth);

            for(int i=0;i<minDepth;i++){
                trials[i]++;
                if(originalCategory.getPart(i).equals(answerCategory.getPart(i))){
                    successes[i]++;
                }
            }
        }

        double[] accuracyPerLevel = new double[depthConsidered];
        for(int i=0;i<depthConsidered;i++){
            if(trials[i]==0){
                accuracyPerLevel[i]=0;
            } else {
                accuracyPerLevel[i]=((double)successes[i])/((double)trials[i]);
            }
        }
        return accuracyPerLevel;
    }
}

package uk.ac.cam.cl.retailcategorymapper.controller;

import uk.ac.cam.cl.retailcategorymapper.classifier.NaiveBayesDbClassifier;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.ClassifyResponse;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.Request;
import uk.ac.cam.cl.retailcategorymapper.entities.Response;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainRequest;
import uk.ac.cam.cl.retailcategorymapper.entities.TrainResponse;
import uk.ac.cam.cl.retailcategorymapper.manual.ManualMapping;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The mapping controller.
 */
public class Controller {
    /**
     * Classify a list of products using the manual mapper first and then
     * falling back to the Naive Bayes classifier.
     *
     * @param request The ClassifyRequest.
     * @return A ClassifyResponse.
     */
    public ClassifyResponse classify(ClassifyRequest request) {
        Taxonomy taxonomy = request.getTaxonomy();
        List<Product> products = request.getProducts();
        Map<Product, List<Mapping>> results = new HashMap<>();

        Classifier manualMappingClassifier = new ManualMapping(taxonomy);
        Classifier naiveBayesClassifier = new NaiveBayesDbClassifier(taxonomy);

        for (Product product : products) {
            List<Mapping> manualMappings = manualMappingClassifier.classify
                    (product);

            if (manualMappings.size() == 1 &&
                    manualMappings.get(0).getConfidence() == 1.0) {
                results.put(product, Arrays.asList(manualMappings.get(0)));
                continue;
            }

            List<Mapping> classifiedMappings = naiveBayesClassifier.classify
                    (product);

            classifiedMappings.sort(new Mapping.ConfidenceSorter());

            results.put(product,
                    Collections.unmodifiableList(classifiedMappings));
        }

        return new ClassifyResponse(taxonomy, results);
    }

    /**
     * Train the mapping engine using the mappings supplied in the training
     * request.
     *
     * @param request The TrainRequest.
     * @return A TrainResponse.
     */
    public TrainResponse train(TrainRequest request) {
        Taxonomy taxonomy = request.getTaxonomy();

        if (!request.getAddToManualMappings()
                && !request.getAddToTrainingSet()) {
            return new TrainResponse(taxonomy, 0, 0);
        }

        Classifier manualMappingClassifier = new ManualMapping(taxonomy);
        Classifier naiveBayesClassifier = new NaiveBayesDbClassifier(taxonomy);

        List<Mapping> mappings = request.getMappings();

        int trainCountManual = 0;
        int trainCountClassifier = 0;

        for (Mapping mapping : mappings) {
            if (request.getAddToManualMappings()) {
                if (manualMappingClassifier.train(mapping)) {
                    trainCountManual += 1;
                }
            }

            if (request.getAddToTrainingSet()) {
                if (naiveBayesClassifier.train(mapping)) {
                    trainCountClassifier += 1;
                }
            }
        }

        return new TrainResponse(taxonomy,
                trainCountManual, trainCountClassifier);
    }

    /**
     * A generic request handler which determines the correct method of
     * handling the request.
     *
     * @param request The request.
     * @return The response to the request.
     */
    public Response handle(Request request) {
        if (request instanceof ClassifyRequest) {
            return classify((ClassifyRequest) request);
        } else if (request instanceof TrainRequest) {
            return train((TrainRequest) request);
        }

        // Unknown request type
        throw new UnsupportedOperationException();
    }
}

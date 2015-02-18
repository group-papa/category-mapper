package uk.ac.cam.cl.retailcategorymapper.entities;

/**
 * An class to store a response after training the system.
 */
public class TrainResponse extends Response {
    private final int trainCountManual;
    private final int trainCountClassifier;

    public TrainResponse(Taxonomy taxonomy, int trainCountManual,
                         int trainCountClassifier) {
        super(taxonomy);
        this.trainCountManual = trainCountManual;
        this.trainCountClassifier = trainCountClassifier;
    }

    public int getTrainCountManual() {
        return trainCountManual;
    }

    public int getTrainCountClassifier() {
        return trainCountClassifier;
    }
}

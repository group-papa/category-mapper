
public class TrainRequest implements Request {
	
	private boolean addToManualMappings;
	private boolean addToTrainingSet;
	private List<Mapping> mappings;
	
	public TrainRequest(boolean addToManualMappings, boolean addToTrainingSet, List<Mapping> mappings) {
		this.addToManualMappings = addToManualMappings;
		this.addToTrainingSet = addToTrainingSet;
		this.mappings = mappings;
	}
	
	public boolean getAddToManualMappings() {
		return this.addToManualMappings;
	}
	
	public boolean getAddToTrainingSet() {
		return this.addToTrainingSet;
	}
	
	public List<Mapping> getMappings() {
		return this.mappings;
	}
	
}

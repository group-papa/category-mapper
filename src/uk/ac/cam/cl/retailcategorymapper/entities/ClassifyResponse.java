
public class ClassifyResponse implements Response {
	
	private List<Mapping> mappingsMade;
	
	public ClassifyResponse(List<Mapping> mappingsMade) {
		this.mappingsMade = mappingsMade;
	}
	
	public List<Mapping> getMappingsMade() {
		return this.mappingsMade();
	}
}


public class Mapping {
	
	private Product product;
	private Category category;
	private Method method;
	private float confidence;
	
	
	public Mapping(Product product, Category category, Method method, float confidence) {
		this.product = product;
		this.category = category;
		this.method = method;
		this.confidence = confidence;
	}
	
	public Product getProduct() {
		return this.product;
	}
	
	public Category getCategory() {
		return this.category;
	}
	
	public float getConfidence() {
		return this.confidence;
	}
}

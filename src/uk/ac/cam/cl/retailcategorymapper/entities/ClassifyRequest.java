
public class ClassifyRequest implements Request {
	
	private List<Product> productsToClassify;
	
	public ClassifyRequest(List<Product> productsToClassify) {
		this.productsToClassify = productsToClassify;
	}
	
	public List<Product> getProductsToClassify() {
		return this.productsToClassify;
	}
	
}

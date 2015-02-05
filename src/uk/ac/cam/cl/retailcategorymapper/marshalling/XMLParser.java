package uk.ac.cam.cl.retailcategorymapper.marshalling;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Mapping;
import uk.ac.cam.cl.retailcategorymapper.entities.MappingBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Method;
import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class XMLParser {

	/*
	 * Authored by Charlie Barton
	 * 
	 * this class provides several methods for parsing an XML file into our
	 * internal representations which take 2 Files:
	 * 1) .xml
	 * 2) .txt
	 * the .txt describes the format of the XML file and parses it appropriately
	 * 
	 * The XML format descriptor takes the form of lines with a single ':'
	 * separating what we call the field internally from what it is called in the
	 * XML file so a sample line is "our name:XML field;"
	 * 
	 * an example descriptor file is
	 * 
	 * XMLdescritor.txt
	 * 
	 * product:product;
	 * price:productPrice;
	 * id:productSku;
	 * name:productName;
	 * description:productDescription;
	 * category1:none;
	 * attributes:none;
	 * category_split: > ;
	 * 
	 * all 7 of these fields must be provided for parsing a product list
	 * 
	 * additionally for parsing a mapping we need to provide a mapped_category 
	 * 
	 * none after the colon indicates we don't what that field processed for
	 * example it is not in the XML
	 * 
	 * Most of the parsing code was copied from:
	 * http://stackoverflow.com/questions
	 * /13786607/normalization-in-dom-parsing-with-java-how-does-it-work
	 * 
	 * (under an open-source licence) so I don't really understand what it is doing but it
	 * seems to work :)
	 */


	static public List<Mapping> parseMapping (File fileXML, File fileXMLFormat){		
		try {			

			HashMap<String,String> descriptorTags = DescriptorParse.parse(fileXMLFormat);
			
			testMappingTagsOk(descriptorTags);
			
			List<Mapping> answer = new LinkedList<>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(fileXML);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(descriptorTags.get("product"));
			
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element element = (Element) nNode;
					ProductBuilder productBuild = prepareProductBuilder(element,descriptorTags);
					Product p = productBuild.createProduct();
					MappingBuilder mapBuild = new MappingBuilder();
					mapBuild.setProduct(p);

					String s = element.getElementsByTagName(descriptorTags.get("mapped_category")).item(0).getTextContent();					
					mapBuild.setCategory(prepareCategory(s,descriptorTags.get("category_split")));
					mapBuild.setMethod(Method.TRAINING);
					
					answer.add(mapBuild.createMapping());					
				}				
			}			
			return answer;			
		} 
		
		/*
		 * although I just re-throw the exception they are change to RuntimeException
		 * which I'm sure someone is going to thank me for
		 */		
	
		catch (SAXException e) {
			System.err.println("SAX Exception in XML Parser");
			throw new RuntimeException("SAX Exception in XML Parser");
		} catch (IOException e) {
			System.err.println("IOException in XML Parser");
			throw new RuntimeException("IOException in XML Parser");
		} catch (ParserConfigurationException e) {
			System.err.println("SAX Exceptionin XML Parser");
			throw new RuntimeException("ParserConfigurationException in XML Parser");
		}		
	}

	
	static public List<Product> parseProductList (File fileXML, File fileXMLFormat){
		
		try {			

			HashMap<String,String> descriptorTags = DescriptorParse.parse(fileXMLFormat);
			
			testProductListTagsOk(descriptorTags);
			
			List<Product> answer = new LinkedList<>();
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc;
			doc = dBuilder.parse(fileXML);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName(descriptorTags.get("product"));
			
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					ProductBuilder productBuild = prepareProductBuilder((Element) nNode,descriptorTags);
					answer.add(productBuild.createProduct());					
				}
				
			}			
			return answer;			
		} 
		
		/*
		 * although I just re-throw the exception they are change to RuntimeException
		 * which I'm sure someone is going to thank me for
		 */		
		catch (SAXException e) {
			System.err.println("SAX Exception in XML Parser");
			throw new RuntimeException("SAX Exception in XML Parser");
		} catch (IOException e) {
			System.err.println("IOException in XML Parser");
			throw new RuntimeException("IOException in XML Parser");
		} catch (ParserConfigurationException e) {
			System.err.println("SAX Exceptionin XML Parser");
			throw new RuntimeException("ParserConfigurationException in XML Parser");
		}		
	}
	
	
	/*
	 * this method takes an element and a hashMap and extracts the relevant fields
	 * out of the element to build a product which it returns
	 */	
	static ProductBuilder prepareProductBuilder(Element element,HashMap<String,String> descriptorTags){
	
		ProductBuilder productBuild = new ProductBuilder();
		
		if(descriptorTags.get("price")!="none"){
			String price = element.getElementsByTagName(descriptorTags.get("price")).item(0).getTextContent();
			productBuild.setPrice((int)(Double.valueOf(price)*100));		
		}

		if(descriptorTags.get("id")!="none"){					
			productBuild.setId(element.getElementsByTagName(descriptorTags.get("id")).item(0).getTextContent());		
		}
		
		if(descriptorTags.get("name")!="none"){
			productBuild.setName(element.getElementsByTagName(descriptorTags.get("name")).item(0).getTextContent());					
		}
		
		if(descriptorTags.get("name")!="none"){
			productBuild.setDescription(element.getElementsByTagName(descriptorTags.get("description")).item(0).getTextContent());			
		}
							
		if(descriptorTags.get("category")!="none"){
			String catPath = element.getElementsByTagName(descriptorTags.get("category")).item(0).getTextContent();	
			Category c = prepareCategory(catPath,descriptorTags.get("category_split"));
			productBuild.setOriginalCategory(c);
		}
		
		if(!descriptorTags.get("attributes").equals("none")){
			throw new RuntimeException("attempt to use read attribute data - currently unimplemented in XML Parser");
			//TODO implement		
		}
		
		return productBuild;
	}
	
	static Category prepareCategory(String catPath,String split){
		String[] asList = catPath.split(split);		
		CategoryBuilder catBuild = new CategoryBuilder();
		catBuild.setParts(asList);
		return catBuild.createCategory();
	}
	
	/*
	 * this method takes a hashMap (producted by the descriptor file) and checks it
	 * contains the necessary fields
	 */	
	static boolean testProductListTagsOk(HashMap<String, String> tagsToTest) {
		if(!tagsToTest.containsKey("price")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"price\" field");
		}
		if(!tagsToTest.containsKey("id")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"id\" field");
		}
		if(!tagsToTest.containsKey("name")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"name\" field");
		}
		if(!tagsToTest.containsKey("description")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"description\" field");
		}
		if(!tagsToTest.containsKey("category")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"category\" field");
		}
		if(!tagsToTest.containsKey("attributes")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"attributes\" field");
		}
		if(!tagsToTest.containsKey("product")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"product\" field");
		}
		if(!tagsToTest.containsKey("category_split")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"category_split\" field");
		}
		return true;
	}
	
	static boolean testMappingTagsOk(HashMap<String, String> tagsToTest){
		testProductListTagsOk(tagsToTest);
		if(!tagsToTest.containsKey("mapped_category")){
			throw new RuntimeException("malformed XMLdescriptor file in XML parser - no \"mapped_category\" field");
		}
		return true;
	}
	
	
	public static void main(String argv[]) {
				
		//obviously file path is only hard coded as this is a main method for testing
		String XMLPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\138.xml";
		String descriptionPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\descriptor.txt";
		List<Mapping> map = parseMapping(new File(XMLPath),new File(descriptionPath));
		
		
		for(Mapping m : map){
			Product p = m.getProduct();
			System.out.println("title           : "+p.getName());
			System.out.println("product ID      : "+p.getId());
			System.out.println("description     : "+p.getDescription());
			System.out.println("price (GBp)     : "+p.getPrice());
			System.out.println("initial category: "+p.getOriginalCategory().toString(" -> "));
			System.out.println("new     category: "+m.getCategory().toString(" -> "));
			System.out.println("");
			System.out.println(" - - - - - - - - - - - - - ");
			System.out.println("");
		}
		
		
		System.out.println("execution complete");

	}

}
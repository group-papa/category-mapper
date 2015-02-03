package uk.ac.cam.cl.retailcategorymapper.marshalling;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import uk.ac.cam.cl.retailcategorymapper.entities.Product;
import uk.ac.cam.cl.retailcategorymapper.entities.ProductBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class XMLParser {

	/*
	 * Authored by Charlie
	 * 
	 * this class provides a method for parsing an XML file into our internal
	 * product description class it takes a file path to an XML file and also a
	 * file path to a file describing the format of the XML file and parses it
	 * appropriately
	 * 
	 * The XML format descriptor takes the form of lines with a single ':'
	 * separating what we call the field internally from what it is called in the
	 * XML file ie "our name:XML field" . so an example file is
	 *
	 * XMLdescritor.txt
	 * 
	 * product:product
   * price:productPrice
   * id:productSku
   * name:productName
   * description:productDescription
   * category:none
   * attributes:none
	 * 
	 * all 7 of these fields must be provided - none after the colon indicated 
	 * we don't what that field processed
	 * 
	 * Most of the code was copied from some web-site so I don't really understand
	 * what it is doing but it seems to work.
	 */

	static public List<Product> parseXML(File fileXML, File fileXMLFormat){
		
		try {			

			HashMap<String,String> descriptorTags = parseXMLDescriptor(fileXMLFormat);
			
			testTagsOk(descriptorTags);
			
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
	 * this method produces a hashMap which means that general XML formats can be
	 * used to read in product data - it produces a hashmap converting what we
	 * internally name the fields to what they are named in the XML
	 */	
	static HashMap<String, String> parseXMLDescriptor(File f) {

		try {
			HashMap<String, String> tags = new HashMap<>();

			BufferedReader descriptorReader = new BufferedReader(new FileReader(f));

			String s = descriptorReader.readLine();
			while (s != null) {

				String[] t = s.split(":");

				if (t.length != 2) {
					descriptorReader.close();
					throw new RuntimeException("malformed XMLdescriptor file in XML parser - not 2 arguments per line");
				}
				tags.put(t[0], t[1]);
				s = descriptorReader.readLine();
			}
			descriptorReader.close();
			return tags;
		} catch (IOException e) {
			throw new RuntimeException("IO exception in XML file format description parser");
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
							
		if(!descriptorTags.get("category").equals("none")){
			throw new RuntimeException("attempt to use read category data - currently unimplemented in XML Parser");
			//TODO implement		
		}
		
		if(!descriptorTags.get("attributes").equals("none")){
			throw new RuntimeException("attempt to use read attribute data - currently unimplemented in XML Parser");
			//TODO implement		
		}
		
		return productBuild;
	}
	
	/*
	 * this method takes a hashMap (producted by the descriptor file) and checks it
	 * contains the necessary fields
	 */	
	static boolean testTagsOk(HashMap<String, String> tagsToTest) {
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
		return true;
	}
	
	
	public static void main(String argv[]) {
		
		//obviously file path is only hard coded as this is a main method for testing
		String XMLPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\2.xml";
		String descriptionPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\descriptor.txt";
		List<Product> products = parseXML(new File(XMLPath),new File(descriptionPath));
		
		
		for(Product p : products){
			System.out.println(p.getName());
			System.out.println("product ID "+p.getId());
			System.out.println(p.getDescription());
			System.out.println("price (GBp) "+p.getPrice());
			System.out.println("");
		}
		

	}

}
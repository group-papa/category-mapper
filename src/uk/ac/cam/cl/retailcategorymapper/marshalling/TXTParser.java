package uk.ac.cam.cl.retailcategorymapper.marshalling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;

import uk.ac.cam.cl.retailcategorymapper.entities.Category;
import uk.ac.cam.cl.retailcategorymapper.entities.CategoryBuilder;
import uk.ac.cam.cl.retailcategorymapper.entities.Taxonomy;
import uk.ac.cam.cl.retailcategorymapper.entities.TaxonomyBuilder;

public class TXTParser{
	
	/*
	 * Authored by Charlie Barton
	 * 
	 * this class provides several methods for parsing an XML file into our
	 * internal representations which take 2 Files:
	 * 1) .txt
	 * 2) .txt
	 * the second .txt describes the format of the first .txt file
	 * 
	 * XMLdescritor.txt
	 * 
	 * header_length:1;
	 * category_split: > ;
	 * 
	 * the category_split must be provided
	 * the header length gives the line number of the first proper line of category
	 */
	
	public static Taxonomy parseTaxonomy(File fileData, File fileDescriptor) {

		HashMap<String, String> descriptorTags = DescriptorParse.parse(fileDescriptor);

		HashMap<String, String> repeatCatcher = new HashMap<>();

		testTagsOk(descriptorTags);

		try {
			BufferedReader dataReader = new BufferedReader(new FileReader(fileData));
			TaxonomyBuilder taxBuild = new TaxonomyBuilder();
			taxBuild.setName(dataReader.readLine());

			if (descriptorTags.containsKey("header_length")) {
				int skip = Integer.valueOf(descriptorTags.get("header_length"));
				if (skip < 1) {
					dataReader.close();
					throw new RuntimeException("malformed txt descriptor - invalid header_length");
				}
				for (int i = 1; i < skip; i++) {
					dataReader.readLine();
				}
			}
			
			String s = dataReader.readLine();
			while (s != null) {
				
				String[] asArray = s.split(descriptorTags.get("category_split"));
				
				//start of repeat catch		
				for(int i=0;i<asArray.length;i++){
					if(repeatCatcher.containsKey(asArray[i])){
						asArray[i]=repeatCatcher.get(asArray[i]);
					} else {
						repeatCatcher.put(asArray[i], asArray[i]);
					}
				}
				//end of repeat catch
				
				CategoryBuilder catBuild = new CategoryBuilder();
				//TODO issue with immutable taxonomy and category class since they both reference each other
				catBuild.setParts(asArray);
				taxBuild.addCategory(catBuild.createCategory());	
				s = dataReader.readLine();
			}

			dataReader.close();
			return taxBuild.createTaxonomy();
		} catch (FileNotFoundException e1) {
			throw new RuntimeException("failed to find TXT file in parse TXT");
		} catch (IOException e1) {
			throw new RuntimeException("I/O exception in txt parser");
		} catch (NumberFormatException e) {
			throw new RuntimeException("malformed txt descriptor - invalid header_length");
		}

	}
	

	static boolean testTagsOk(HashMap<String, String> tagsToTest) {
		if(!tagsToTest.containsKey("category_split")){
			throw new RuntimeException("malformed TXT Taxonomy descriptor file in XML parser - no \"category_split\" field");
		}
		return true;
	}

	public static void main(String[] args) {

		// obviously file path is only hard coded as this is a main method for
		// testing
		
		long startTime = System.currentTimeMillis();
		String TXTPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\GoogleTax2013.12.12.txt";
		String descriptionPath = "C:\\Users\\Charlie\\SkyDrive\\Documents\\Cambridge Work\\IB\\Group Project\\TaxDescriptor.txt";
		Taxonomy tax = parseTaxonomy(new File(TXTPath), new File(descriptionPath));
		long endTime = System.currentTimeMillis();
		
		
		List<Category> cats = tax.getCategories();
		for (Category c : cats) {
			System.out.println(c.toString(" -> "));
		}

		
		System.gc();
		MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
		double mem_usage = heapMemoryUsage.getUsed();
		System.out.println((mem_usage/(1024*1024))+" MB used");
		System.out.println(endTime-startTime+" ms taken");
		
		System.out.println("execution complete");

		
	}

}

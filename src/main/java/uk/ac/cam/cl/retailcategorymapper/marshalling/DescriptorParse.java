package uk.ac.cam.cl.retailcategorymapper.marshalling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class DescriptorParse {
	
	/*
	 * this method produces a hashMap which means that general formats can be used
	 * to read in product data - it produces a hashmap converting internally used
	 * string fields to those used in the input file
	 */
	static HashMap<String, String> parse(File f) {

		try {
			HashMap<String, String> tags = new HashMap<>();

			BufferedReader descriptorReader = new BufferedReader(new FileReader(f));

			String s = descriptorReader.readLine();
			while (s != null) {

				if(!s.endsWith(";")){
					descriptorReader.close();
					throw new RuntimeException("malformed descriptor file - did you forget a ;");
				}				
				s = s.substring(0,s.length()-1);
				
				String[] t = s.split(":");

				if (t.length != 2) {
					descriptorReader.close();
					throw new RuntimeException("malformed descriptor file - not 2 arguments per line");
				}
				tags.put(t[0], t[1]);
				s = descriptorReader.readLine();
			}
			descriptorReader.close();
			return tags;
		} catch (IOException e) {
			throw new RuntimeException("IO exception in file format description parser");
		}
	}
	
	
}

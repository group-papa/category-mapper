package uk.ac.cam.cl.retailcategorymapper.classifier.features;

public class Sanitizer1 {

	public static String sanitize(String s){
		if(s == null){return "";}
		s = removePunctuation(s);
		s = removeCapitals(s);
		return s;
	}

	public static String removeCapitals(String s){
		return s.toLowerCase();
	}

	public static String removePunctuation(String s){
		s = s.replace(")","");
		s = s.replace("(","");
		s = s.replace("/","");
		s = s.replace("\\","");
		s = s.replace("\"","");
		s = s.replace("{","");
		s = s.replace("}","");
		s = s.replace("[","");
		s = s.replace("]","");
		s = s.replace(".","");
		s = s.replace(",","");
		s = s.replace("-","");
		s = s.replace("!","");
		s = s.replace(";","");
		s = s.replace(":","");
		s = s.replace("?","");
		s = s.replace("|","");
		s = s.replace("&","");
		s = s.replace("£","");
		s = s.replace("$","");
		return s;
	}


}

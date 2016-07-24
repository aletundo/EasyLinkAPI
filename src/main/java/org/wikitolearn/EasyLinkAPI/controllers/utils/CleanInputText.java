package org.wikitolearn.EasyLinkAPI.controllers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanInputText {
	
	private static String removeMath(String inputText){
		String cleanText = "";
		Pattern mathTags = Pattern.compile("<(dmath|math)[^>]*>(.+?)</(dmath|math)\\s*>", Pattern.DOTALL);
		Matcher m1 = mathTags.matcher(inputText);
		cleanText = m1.replaceAll("");
		return cleanText;
	}
	
	private static String removeAnnotations(String inputText){
		String cleanText = "";
		Pattern annotationTags = Pattern.compile("<(easylink)[^>]*>(.+?)</(easylink)\\s*>", Pattern.DOTALL);
		Pattern annotationTags2 = Pattern.compile("<(span)[^>]*></(span)\\s*>", Pattern.DOTALL);
		Matcher m1 = annotationTags.matcher(inputText);
		cleanText = m1.replaceAll("$2");
		Matcher m2 = annotationTags2.matcher(cleanText);
		cleanText = m2.replaceAll("");
		return cleanText;
	}

	public static String clean(String inputText){
		String cleanText = removeMath(inputText);
        cleanText = removeAnnotations(cleanText);
        System.out.println(cleanText);
        return cleanText;
	}
}

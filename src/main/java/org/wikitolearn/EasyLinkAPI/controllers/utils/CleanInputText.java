package org.wikitolearn.EasyLinkAPI.controllers.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CleanInputText {
	
	public static String removeMath(String inputText){
		String cleanText = "";
		Pattern mathTags = Pattern.compile("<(dmath|math)[^>]*>(.+?)</(dmath|math)\\s*>", Pattern.DOTALL);
		Matcher m1 = mathTags.matcher(inputText);
		cleanText = m1.replaceAll("");
		//System.out.println(cleanText);
		return cleanText;
	}
}

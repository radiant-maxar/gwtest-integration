package geowave.util;

import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class TestUtils {
	// To make tests cleaner.  See SO https://stackoverflow.com/questions/86780/
	public static boolean insensitiveMatch(String fullString, String targetString) {
		Pattern p = Pattern.compile(Pattern.quote(targetString), Pattern.CASE_INSENSITIVE);
		return p.matcher(fullString).find();
	}
	
	// Assert that the response was successful
	public static void assertSuccess(String response) {
		assertNotNull("Response should not be null", response);
		assertTrue("Response should not contain exception", !insensitiveMatch(response, "exception"));
		assertTrue("Response should not contain error", !insensitiveMatch(response, "error"));
	}
}

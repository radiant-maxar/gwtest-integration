package geowave.util;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Pattern;

import static org.junit.Assert.*;


public class TestUtils {
	// To make tests cleaner.  See SO https://stackoverflow.com/questions/86780/
	public static boolean insensitiveMatch(String fullString, String targetString) {
		Pattern p = Pattern.compile(Pattern.quote(targetString), Pattern.CASE_INSENSITIVE);
		System.out.println("OUTPUT 2 MATCH: " + fullString);
		return p.matcher(fullString).find();
	}
	
	// Assert that the response was successful
	public static void assertSuccess(String response) {
		assertNotNull("Response should not be null", response);
		if (insensitiveMatch(response, "exception")) {
			System.out.println("R <EXCEPTION>: " + response);
			fail("Response should not contain exception");
		} else if (insensitiveMatch(response, "error")){
			System.out.println("R <ERROR>: " + response);
//			fail("Response should not contain error");
		} else {
			System.out.println("R <SUCCESS>:" + response); // always print response, for debugging
			// pass
		}
	}
	
	// Return the status code from a GET
	public static int get(String url) {
		try {
			URL u = new URL(url);
			HttpURLConnection c = (HttpURLConnection) u.openConnection();
			c.setRequestMethod("HEAD");
			c.connect();
			int code = c.getResponseCode();
			System.out.println("CODE: " + code);
			System.out.println("MSG: " + c.getResponseMessage());
			return code;
		} catch (IOException e) {
			System.out.println("Error Message: " + e.getMessage());
			return 0;
		}
	}
	
	// Wait until URL returns 200
	public static boolean tryUntilOK(String url, int timeoutSeconds) {
		while (timeoutSeconds >= 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				assert false;
			}
			
			if (get(url) == 200) {
				return true;
			}
			timeoutSeconds--;
		}
		return false;
	}
	
}

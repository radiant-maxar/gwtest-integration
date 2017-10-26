package geowave.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.json.*;


public class CmdUtils {
	
	// Send a command, and get the response back.
	// TODO: Timeout
	public static String send(int timeout, String[] cmd, String... vars) {
		Process p;
		try {
			p = createCmdProcess(cmd, vars);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("C: " + Arrays.toString(cmd));
		InputStream is = p.getInputStream();
		try {
			String output = IOUtils.toString(is, "UTF-8");
			System.out.println("R: " + output);
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	public static String send(String... cmd) {
		// Default wait 1 minute.
		return send(60000, cmd);
	}
	public static String send(int timeout, String... cmd) {
		// Default wait 1 minute.
		return send(timeout, cmd, new String[]{});
	}
	public static String send(String[] vars, String... cmd) {
		// Default wait 1 minute.
		return send(60000, cmd, vars);
	}
	
	public static String shellCmd(String cmd) {
		String responses = "";
		responses += send("echo '" + cmd + "' > temp.sh");
		responses += send("/bin/sh temp.sh");
		responses += send("rm temp.sh");
		return responses;
	}
	
	public static String getProperty(String response, String propertyRegEx) {
		// Get string after property name + colon
		propertyRegEx += ".*:";
		String[] matches = response.split(propertyRegEx);
		
		// If no match, return empty string
		String match;
		if (matches.length < 2) {
			return "";
		} else {
			match = matches[1];
		}
		
		// trim match
		return match.split("\n")[0].trim();
	}
	
	public static String getJSONProperty(String json, String property) {
		System.out.println("R: " + json);
		JSONObject j = new JSONObject(json);
		return (String) j.get(property);
	}
	
	public static boolean JSONcontains(String json, String property) {
		System.out.println("R: " + json);
		JSONObject j = new JSONObject(json);
		return j.has(property);
	}
	
	private static Process createCmdProcess(String[] cmd, String[] vars) throws Exception {
		if (cmd.length == 1) {
			return Runtime.getRuntime().exec(cmd[0], vars);
		} else if (cmd.length > 1) {
			return Runtime.getRuntime().exec(cmd, vars);
		} else {
			throw new Exception("Must have at least one string command.");
		}
	}
	
	
}

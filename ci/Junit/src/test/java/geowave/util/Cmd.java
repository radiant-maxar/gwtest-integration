package geowave.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.json.*;


public class Cmd {
	
	private String[] vars;
	
	// When initializing the Cmd object, select the environment variables to use.
	public Cmd() {
		resetVars();
	}
	public Cmd(String[] vars, boolean includeEnv) {
		setVars(vars, includeEnv);
	}
	
	// Send a command, and get the response back.
	// TODO: Timeout
	public String send(String... cmd) {
		Process p;
		try {
			p = createCmdProcess(cmd, vars);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		System.out.println("C: " + Arrays.toString(cmd));
		InputStream is = p.getInputStream();
		InputStream es = p.getErrorStream();
		try {
			String output = IOUtils.toString(es, "UTF-8") + IOUtils.toString(is, "UTF-8");
			return output;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String shellCmd(String cmd) {
		String responses = "";
		responses += send("echo '" + cmd + "' > temp.sh");
		responses += send("/bin/sh temp.sh");
		responses += send("rm temp.sh");
		return responses;
	}
	
	public String getProperty(String response, String propertyRegEx) {
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
	
	public String getJSONProperty(String json, String property) {
		System.out.println("R: " + json);
		JSONObject j = new JSONObject(json);
		return (String) j.get(property);
	}
	
	public boolean JSONcontains(String json, String property) {
		System.out.println("R: " + json);
		JSONObject j = new JSONObject(json);
		return j.has(property);
	}
	
	private Process createCmdProcess(String[] cmd, String[] vars) throws Exception {
		if (cmd.length == 1) {
			return Runtime.getRuntime().exec(cmd[0], vars);
		} else if (cmd.length > 1) {
			return Runtime.getRuntime().exec(cmd, vars);
		} else {
			throw new Exception("Must have at least one string command.");
		}
	}
	
	private static String[] environment() {
		Map<String, String> env = System.getenv();
		String[] outputEnvironemnt = new String[env.size()];
		int i=0;
		for (String envName : env.keySet()) {
			outputEnvironemnt[i] = String.format("%s=%s", envName, env.get(envName));
			i++;
		}
		return outputEnvironemnt;
	}
	
	public void resetVars() {
		this.vars = environment();
	}
	
	public void setVars(String[] vars, boolean includeEnv) {
		if (includeEnv) {
			this.vars = (String[]) ArrayUtils.addAll(vars, environment());
		} else {
			this.vars = vars;
		}
	}
}

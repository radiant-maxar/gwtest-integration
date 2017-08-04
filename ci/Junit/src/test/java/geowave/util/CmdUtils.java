package geowave.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

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
		Scanner s = new Scanner(is);
		s.useDelimiter("\\A");
		String response = s.hasNext() ? s.next() : "";
		s.close();
		return response;
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

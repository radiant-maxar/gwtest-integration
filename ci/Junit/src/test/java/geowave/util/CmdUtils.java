package geowave.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CmdUtils {
	
	// Send a command, and get the response back.
	// TODO: Timeout
	public static String send(int timeout, String... cmd) {
		Process p;
		try {
			p = createCmdProcess(cmd);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	
	private static Process createCmdProcess(String[] cmd) throws Exception {
		if (cmd.length == 1) {
			return Runtime.getRuntime().exec(cmd[0]);
		} else if (cmd.length > 1) {
			return Runtime.getRuntime().exec(cmd);
		} else {
			throw new Exception("Must have at least one string command.");
		}
	}
}

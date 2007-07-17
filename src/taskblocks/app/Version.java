package taskblocks.app;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Version {

	// Application version. On runtime read from file which is generated when building the application.
	public static final String VERSION;
	
	static {
		String ver ="unknown";
		try {
			InputStream versionResourceStream = ClassLoader.getSystemResourceAsStream("taskblocks/version");
			if(versionResourceStream != null) {
				BufferedReader r = new BufferedReader(new InputStreamReader(versionResourceStream));
				ver=r.readLine();
			}
		} catch (Exception e) {
		}
		VERSION=ver;
	}
}

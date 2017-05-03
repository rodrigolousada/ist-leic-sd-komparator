package pt.ulisboa.tecnico.sdis.ws.it;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

public class BaseIT {

	private static final String TEST_PROP_FILE = "/test.properties";
	protected static Properties testProps;

	protected static CAClient CLIENT;

	@BeforeClass
	public static void oneTimeSetup() throws Exception {
		testProps = new Properties();
		try {
			testProps.load(BaseIT.class.getResourceAsStream(TEST_PROP_FILE));
			System.out.println("Test properties:");
			System.out.println(testProps);
		} catch (IOException e) {
			final String msg = String.format("Could not load properties file {}", TEST_PROP_FILE);
			System.out.println(msg);
			throw e;
		}

		String uddiEnabled = testProps.getProperty("uddi.enabled");
		String uddiURL = testProps.getProperty("uddi.url");
		String wsName = testProps.getProperty("ws.name");
		String wsURL = testProps.getProperty("ws.url");

		if ("true".equalsIgnoreCase(uddiEnabled)) {
			CLIENT = new CAClient(uddiURL, wsName);
		} else {
			CLIENT = new CAClient(wsURL);
		}
		// CLIENT.setVerbose(true);

	}

	@AfterClass
	public static void oneTimeCleanup() {
	}

}

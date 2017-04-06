package org.komparator.supplier.ws;

/** Main class that starts the Supplier Web Service. */
public class SupplierApp {

	public static void main(String[] args) throws Exception {
		// Check arguments
		if (args.length < 1) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + SupplierApp.class.getName() + " wsURL wsName wsUDDIaddr");
			return;
		}		
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

		// Create server implementation object, according to options
		SupplierEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new SupplierEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			endpoint = new SupplierEndpointManager(uddiURL, wsName, wsURL);
			endpoint.setVerbose(true);
		}

		// Create server implementation object
		try {
			endpoint.start();
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
			System.out.printf("Deleted '%s' from UDDI%n", wsName);
		}

	}
}

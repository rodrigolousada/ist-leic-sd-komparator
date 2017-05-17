package org.komparator.mediator.ws;

import java.util.Timer;

public class MediatorApp {

	public static void main(String[] args) throws Exception {
		
		LifeProof lifeProof = null;
		Timer timer = null;
		
		// Check arguments
		if (args.length == 0 || args.length == 2) {
			System.err.println("Argument(s) missing!");
			System.err.println("Usage: java " + MediatorApp.class.getName() + " wsURL OR uddiURL wsName wsURL");
			return;
		}
		String uddiURL = null;
		String wsName = null;
		String wsURL = null;

		// Create server implementation object, according to options
		MediatorEndpointManager endpoint = null;
		if (args.length == 1) {
			wsURL = args[0];
			endpoint = new MediatorEndpointManager(wsURL);
		} else if (args.length >= 3) {
			uddiURL = args[0];
			wsName = args[1];
			wsURL = args[2];
			
			if(wsURL.equals("http://localhost:8071/mediator-ws/endpoint")){
				System.out.println("Primary Mediator\n");
			}
			else if(wsURL.equals("http://localhost:8072/mediator-ws/endpoint")){
				System.out.println("Secundary Mediator\n");
			}
			else
				System.out.println("unknow mediator");
			
			endpoint = new MediatorEndpointManager(uddiURL, wsName, wsURL);
			endpoint.setVerbose(true);
		}

		try {
			endpoint.start();

			timer = new Timer(/*isDaemon*/ true);
			lifeProof = new LifeProof(endpoint);
			timer.schedule(lifeProof, /*delay*/ 5 * 1000, /*period*/ 5 * 1000);
			
			endpoint.awaitConnections();
		} finally {
			endpoint.stop();
			timer.cancel();
			lifeProof.cancel();
		}

	}

}

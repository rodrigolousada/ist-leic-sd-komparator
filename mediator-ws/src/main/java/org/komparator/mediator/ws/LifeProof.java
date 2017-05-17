package org.komparator.mediator.ws;

import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {
	
	private MediatorClient mediatorClient;
	private boolean primary = false;
	
	public LifeProof(String wsURL) {
		if(wsURL.equals("http://localhost:8071/mediator-ws/endpoint"))
			primary = true;
	}
	
	@Override
	public void run() {
		
		if(primary) {
			//if() {
				try {
					mediatorClient = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
				} catch (MediatorClientException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			//}
			mediatorClient.imAlive();
		}
		else {
			//TODO
		}
	}
}

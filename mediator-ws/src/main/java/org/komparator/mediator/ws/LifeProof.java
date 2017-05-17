package org.komparator.mediator.ws;

import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {
	
	private MediatorClient mediatorClient;
	
	public LifeProof() {
		
	}
	
	@Override
	public void run() {
		
		try {
			mediatorClient = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
		} catch (MediatorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

package org.komparator.mediator.ws;

import java.util.Timer;
import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {
	
	private MediatorClient mediatorClient;
	
	public LifeProof(String wsURL) {
		Timer timer = new Timer(true);
		timer.schedule(this, 5000, 5000);
	}
	
	@Override
	public void run() {
		
		try {
			mediatorClient = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			mediatorClient.imAlive();
		} catch (MediatorClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

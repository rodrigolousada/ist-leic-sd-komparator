package org.komparator.mediator.ws;

import java.util.Date;
import java.util.TimerTask;

import org.komparator.mediator.ws.cli.MediatorClient;
import org.komparator.mediator.ws.cli.MediatorClientException;

public class LifeProof extends TimerTask {
	
	private MediatorEndpointManager mediatorEndpointManager;
	private MediatorClient mediatorClient;
	private boolean primary = false;
	private Date date = new Date();
	
	public LifeProof(MediatorEndpointManager mediatorEndpointManager) {
		this.mediatorEndpointManager = mediatorEndpointManager;
		if(mediatorEndpointManager.getWsURL().equals("http://localhost:8071/mediator-ws/endpoint")) {
			primary = true;
			try {
				mediatorClient = new MediatorClient("http://localhost:8072/mediator-ws/endpoint");
			} catch (MediatorClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run() {		
		if(primary) {
			if(mediatorClient!=null) mediatorClient.imAlive();
		}
		else {
			date = new Date();
			if(mediatorEndpointManager.getLastDate()!=null && date.getTime() - mediatorEndpointManager.getLastDate().getTime() > 8000) {
				System.out.println("Date is not fresh!");
				try {
					mediatorEndpointManager.publishToUDDI();
					primary = true;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			else System.out.println("Fresh date received");
		}
	}
}

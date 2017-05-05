package org.komparator.security.handler;

import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class AttackHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddAttackHandler: Handling message.");
		
		Boolean outboundElement = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		try {
			if (outboundElement.booleanValue()) {
				System.out.println("Writing header in outbound SOAP message...");

				// get SOAP envelope
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();

				SOAPBody sb = se.getBody();
				
				// add header
				SOAPHeader sh = se.getHeader();
				if (sh == null)
					sh = se.addHeader();

				// add header element (name, namespace prefix, namespace)
				Name name = se.createName("cipherCCHeader", "c", "http://org.komparator.A63");
				sh.addHeaderElement(name);
				
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				System.out.println("OPERATION: " + opn.getLocalPart());
				if (!opn.getLocalPart().equals("getProduct")) { return true; }
				
				NodeList children = sb.getFirstChild().getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = children.item(i);
					if (argument.getNodeName().equals("id") && argument.getNodeValue().equals("X1")) {
						argument.setNodeValue("HACKED");
						msg.saveChanges();
						return true;
					}
				}
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext smc) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void close(MessageContext smc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}

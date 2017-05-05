package org.komparator.security.handler;

import static javax.xml.bind.DatatypeConverter.parseBase64Binary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;

import java.io.ByteArrayOutputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Iterator;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.komparator.security.CryptoUtil;
import org.komparator.security.SingletonSecurity;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;

public class SignatureHandler implements SOAPHandler<SOAPMessageContext> {

	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddSignatureHandler: Handling message.");
		
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
				Name name = se.createName("nameHeader", "n", "http://org.komparator.A63");
				SOAPHeaderElement element = sh.addHeaderElement(name);
				
				if(!SingletonSecurity.getInstance().getWsName().equals(""))
					element.addTextNode(SingletonSecurity.getInstance().getWsName());
				msg.saveChanges();
				
				Node argument = sb.getFirstChild();
				String secretArgument = argument.getTextContent();
				PrivateKey privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource(SingletonSecurity.getInstance().getWsName() + ".jks", "xrr017DJ".toCharArray(), SingletonSecurity.getInstance().getWsName().toLowerCase(), "xrr017DJ".toCharArray());
				byte[] digitalSignature = CryptoUtil.makeDigitalSignature("SHA256withRSA", privateKey, secretArgument.getBytes());
				
				Name signatureName = se.createName("signatureHeader", "s", "http://org.komparator.A63");
				SOAPHeaderElement signatureElement = sh.addHeaderElement(signatureName);
				signatureElement.addTextNode(printBase64Binary(digitalSignature));
				msg.saveChanges();
			}
			else {
				System.out.println("Reading header in inbound SOAP message...");

				// get SOAP envelope header
				SOAPMessage msg = smc.getMessage();
				SOAPPart sp = msg.getSOAPPart();
				SOAPEnvelope se = sp.getEnvelope();
				SOAPBody sb = se.getBody();
				SOAPHeader sh = se.getHeader();

				// check header
				if (sh == null) {
					System.out.println("Header not found.");
					return true;
				}
				
				// get first header element
				Name name = se.createName("nameHeader", "n", "http://org.komparator.A63");
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();
				
				String wsName = element.getValue();
				
				// get second header element
				Name name2 = se.createName("signatureHeader", "s", "http://org.komparator.A63");
				Iterator it2 = sh.getChildElements(name);
				// check header element
				if (!it2.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element2 = (SOAPElement) it2.next();
				
				
				String signature = element2.getValue();
				byte[] signatureBytes = parseBase64Binary(signature);
				
				
				sh.removeChild(element2);
				msg.saveChanges();
				String msgString = msg.toString();
				byte[] msgBytes = msgString.getBytes();
				
				
				//
				
				PublicKey publicKey;
				
				CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
				String certificateString = ca.getCertificate(wsName);
				java.security.cert.Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(certificateString);
				
				java.security.cert.Certificate confirmationCertificate = CryptoUtil.getX509CertificateFromResource("ca.cer");
				PublicKey confirmationPublicKey = CryptoUtil.getPublicKeyFromCertificate(confirmationCertificate);
				if(!CryptoUtil.verifySignedCertificate(confirmationCertificate, confirmationPublicKey))
					throw new RuntimeException();
				
				publicKey = CryptoUtil.getPublicKeyFromCertificate(certificate);
				
				//
				
				CryptoUtil.verifyDigitalSignature("SHA256withRSA", publicKey, msgBytes, signatureBytes);
			}
		} catch (Exception e) {
			System.out.print("Caught exception in handleMessage: ");
			System.out.println(e);
			System.out.println("Continue normal processing...");
		}

		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void close(MessageContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<QName> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

}

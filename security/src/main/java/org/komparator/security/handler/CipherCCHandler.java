package org.komparator.security.handler;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Set;

import javax.crypto.Cipher;
import javax.security.cert.Certificate;
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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import pt.ulisboa.tecnico.sdis.ws.cli.CAClient;
import pt.ulisboa.tecnico.sdis.ws.cli.CAClientException;

import static javax.xml.bind.DatatypeConverter.printBase64Binary;
import static javax.xml.bind.DatatypeConverter.parseBase64Binary;

public class CipherCCHandler implements SOAPHandler<SOAPMessageContext> {

	public static final String CONTEXT_PROPERTY = "my.property";
	CryptoUtil cryptoUtil = new CryptoUtil();
	
	@Override
	public boolean handleMessage(SOAPMessageContext smc) {
		System.out.println("AddCypherCCHandler: Handling message.");
		
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
				SOAPHeaderElement element = sh.addHeaderElement(name);
				
				QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				System.out.println("OPERATION: " + opn.getLocalPart());
				if (!opn.getLocalPart().equals("buyCart")) { return true; }
				
				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = children.item(i);
					if (argument.getNodeName().equals("creditCardNr")) {
						String secretArgument = argument.getTextContent();
						System.out.println("NUMEROCCANTES: "+secretArgument);
						// cipher message with symmetric key
						byte[] byteOut = secretArgument.getBytes();
						byte[] cipheredArgument = CryptoUtil.asymcipher(byteOut, getOutboundPublicKey());
						
						String encodedSecretArgument = printBase64Binary(cipheredArgument);
						System.out.println("CIFRADO: "+encodedSecretArgument);
						argument.setTextContent(encodedSecretArgument);
						msg.saveChanges();
						return true;
					}
				}
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
				Name name = se.createName("cipherCCHeader", "c", "http://org.komparator.A63");
				Iterator it = sh.getChildElements(name);
				// check header element
				if (!it.hasNext()) {
					System.out.println("Header element not found.");
					return true;
				}
				SOAPElement element = (SOAPElement) it.next();
				
				QName svcn = (QName) smc.get(MessageContext.WSDL_SERVICE);
				QName opn = (QName) smc.get(MessageContext.WSDL_OPERATION);
				
				if (!opn.getLocalPart().equals("buyCart")) { return true; }
				
				NodeList children = sb.getFirstChild().getChildNodes();
				
				for (int i = 0; i < children.getLength(); i++) {
					Node argument = children.item(i);
					if (argument.getNodeName().equals("creditCardNr")) {
						String secretArgument = argument.getTextContent();
						
						// cipher message with symmetric key
						byte[] decodeSecretArgument = parseBase64Binary(secretArgument);
						byte[] decipheredArgument = CryptoUtil.asymdecipher(decodeSecretArgument, getInboundPrivateKey());
						String decipheredString = new String(decipheredArgument);
						System.out.println("NUMEROCC: "+decipheredString);
						argument.setTextContent(decipheredString);
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
	
	
	private PublicKey getOutboundPublicKey() throws CAClientException, CertificateException, IOException {
		CAClient ca = new CAClient("http://sec.sd.rnl.tecnico.ulisboa.pt:8081/ca");
		String certificateString = ca.getCertificate("A63_Mediator");
		java.security.cert.Certificate certificate = CryptoUtil.getX509CertificateFromPEMString(certificateString);
		
		java.security.cert.Certificate confirmationCertificate = CryptoUtil.getX509CertificateFromResource("ca.cer");
		PublicKey confirmationPublicKey = CryptoUtil.getPublicKeyFromCertificate(confirmationCertificate);
		if(!CryptoUtil.verifySignedCertificate(confirmationCertificate, confirmationPublicKey))
			throw new RuntimeException();
		
		return CryptoUtil.getPublicKeyFromCertificate(certificate);
	}
	
	private PrivateKey getInboundPrivateKey() throws UnrecoverableKeyException, KeyStoreException, CertificateException, IOException {
		PrivateKey privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource("A63_Mediator.jks", "xrr017DJ".toCharArray(), "a63_mediator", "xrr017DJ".toCharArray());
		return privateKey;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		// TODO Auto-generated method stub
		return true;
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

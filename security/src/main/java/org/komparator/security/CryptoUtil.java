package org.komparator.security;

import java.io.*;
import java.nio.charset.StandardCharsets;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;


public class CryptoUtil {
	
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	public static boolean outputFlag = true;

	
    public static byte[] asymcipher(byte[] bytes, PublicKey Publickey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
			Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, Publickey);
			byte [] cipheredBytes = cipher.doFinal(bytes);
			return cipheredBytes;
    }
    
    public static byte[] asymdecipher(byte[] bytes, PrivateKey Privatekey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	Cipher cipher;
		cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, Privatekey);
    	byte[] decipheredBytes;
		decipheredBytes = cipher.doFinal(bytes);
		return decipheredBytes;
    }
    
    public static byte[] makeDigitalSignature(final String signatureMethod, final PrivateKey privateKey,
			final byte[] bytesToSign) {
		try {
			Signature sig = Signature.getInstance(signatureMethod);
			sig.initSign(privateKey);
			sig.update(bytesToSign);
			byte[] signatureResult = sig.sign();
			return signatureResult;
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while making signature: " + e);
				System.err.println("Returning null.");
			}
			return null;
		}
	}

	public static boolean verifyDigitalSignature(final String signatureMethod, PublicKey publicKey,
			byte[] bytesToVerify, byte[] signature) {
		try {
			Signature sig = Signature.getInstance(signatureMethod);
			sig.initVerify(publicKey);
			sig.update(bytesToVerify);
			return sig.verify(signature);
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying signature " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
	}
 	
 	public static Certificate getX509CertificateFromResource(String certificateResourcePath)
			throws IOException, CertificateException {
		InputStream is = getResourceAsStream(certificateResourcePath);
		return getX509CertificateFromStream(is);
	}
 	
 	public static Certificate getX509CertificateFromStream(InputStream in) throws CertificateException {
		try {
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			Certificate cert = certFactory.generateCertificate(in);
			return cert;
		} finally {
			closeStream(in);
		}
	}
 	
 	public static PrivateKey getPrivateKeyFromKeyStoreResource(String keyStoreResourcePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword)
			throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromResource(keyStoreResourcePath, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
	}
 	
 	public static PrivateKey getPrivateKeyFromKeyStore(String keyAlias, char[] keyPassword, KeyStore keystore)
			throws KeyStoreException, UnrecoverableKeyException {
		PrivateKey key;
		try {
			key = (PrivateKey) keystore.getKey(keyAlias, keyPassword);
		} catch (NoSuchAlgorithmException e) {
			throw new KeyStoreException(e);
		}
		return key;
	}
 	
 	public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
	}
 	
 	public static KeyStore readKeystoreFromResource(String keyStoreResourcePath, char[] keyStorePassword)
			throws KeyStoreException {
		InputStream is = getResourceAsStream(keyStoreResourcePath);
		return readKeystoreFromStream(is, keyStorePassword);
	}
 	
 	private static KeyStore readKeystoreFromStream(InputStream keyStoreInputStream, char[] keyStorePassword)
			throws KeyStoreException {
		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		try {
			keystore.load(keyStoreInputStream, keyStorePassword);
		} catch (NoSuchAlgorithmException | CertificateException | IOException e) {
			throw new KeyStoreException("Could not load key store", e);
		} finally {
			closeStream(keyStoreInputStream);
		}
		return keystore;
	}
 	
 	private static InputStream getResourceAsStream(String resourcePath) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
		return is;
	}
 	
 	private static void closeStream(InputStream in) {
		try {
			if (in != null)
				in.close();
		} catch (IOException e) {
			// ignore
		}
	}
 	

	public static Certificate getX509CertificateFromBytes(byte[] bytes) throws CertificateException {
		InputStream in = new ByteArrayInputStream(bytes);
		return getX509CertificateFromStream(in);
	}


	public static Certificate getX509CertificateFromPEMString(String certificateString) throws CertificateException {
		byte[] bytes = certificateString.getBytes(StandardCharsets.UTF_8);
		return getX509CertificateFromBytes(bytes);
	}


	public static Certificate getX509CertificateFromFile(File certificateFile)
			throws FileNotFoundException, CertificateException {
		FileInputStream fis = new FileInputStream(certificateFile);
		return getX509CertificateFromStream(fis);
	}


	public static Certificate getX509CertificateFromFile(String certificateFilePath)
			throws FileNotFoundException, CertificateException {
		File certificateFile = new File(certificateFilePath);
		return getX509CertificateFromFile(certificateFile);
	}

	
	public static Collection<Certificate> getX509CertificatesFromFile(File certificateFile)
			throws FileNotFoundException, CertificateException {
		FileInputStream fis = new FileInputStream(certificateFile);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		@SuppressWarnings("unchecked")
		Collection<Certificate> c = (Collection<Certificate>) cf.generateCertificates(fis);
		return c;

	}

	
	public static Collection<Certificate> getX509CertificatesFromFile(String certificateFilePath)
			throws FileNotFoundException, CertificateException {
		File certificateFile = new File(certificateFilePath);
		return getX509CertificatesFromFile(certificateFile);
	}


	public static PrivateKey getPrivateKeyFromKeyStoreFile(File keyStoreFile, char[] keyStorePassword, String keyAlias,
			char[] keyPassword) throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromFile(keyStoreFile, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
	}

	public static PrivateKey getPrivateKeyFromKeyStoreFile(String keyStoreFilePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword)
			throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		return getPrivateKeyFromKeyStoreFile(new File(keyStoreFilePath), keyStorePassword, keyAlias, keyPassword);
	}

	private static KeyStore readKeystoreFromFile(File keyStoreFile, char[] keyStorePassword)
			throws FileNotFoundException, KeyStoreException {
		FileInputStream fis = new FileInputStream(keyStoreFile);
		return readKeystoreFromStream(fis, keyStorePassword);
	}


	public static KeyStore readKeystoreFromFile(String keyStoreFilePath, char[] keyStorePassword)
			throws FileNotFoundException, KeyStoreException {
		return readKeystoreFromFile(new File(keyStoreFilePath), keyStorePassword);
	}

	
	public static boolean verifyDigitalSignature(final String signatureMethod, Certificate publicKeyCertificate,
			byte[] bytesToVerify, byte[] signature) {
		return verifyDigitalSignature(signatureMethod, publicKeyCertificate.getPublicKey(), bytesToVerify, signature);
	}


	public static boolean verifySignedCertificate(Certificate certificate, PublicKey caPublicKey) {
		try {
			certificate.verify(caPublicKey);
		} catch (InvalidKeyException | CertificateException | NoSuchAlgorithmException | NoSuchProviderException
				| SignatureException e) {
			if (outputFlag) {
				System.err.println("Caught exception while verifying certificate with CA public key : " + e);
				System.err.println("Returning false.");
			}
			return false;
		}
		return true;
	}

	public static boolean verifySignedCertificate(Certificate certificate, Certificate caCertificate) {
		return verifySignedCertificate(certificate, caCertificate.getPublicKey());
	}
}

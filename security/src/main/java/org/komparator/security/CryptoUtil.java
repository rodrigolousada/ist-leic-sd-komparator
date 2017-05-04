package org.komparator.security;

import java.io.*;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;


public class CryptoUtil {
	
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	

	
    public byte[] asymcipher(byte[] bytes, PublicKey Publickey) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException{
			Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
			cipher.init(Cipher.ENCRYPT_MODE, Publickey);
			byte [] cipheredBytes = cipher.doFinal(bytes);
			return cipheredBytes;
    }
    
    public byte[] asymdecipher(byte[] bytes, PrivateKey Privatekey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	Cipher cipher;
		cipher = Cipher.getInstance(ASYM_CIPHER);
		cipher.init(Cipher.DECRYPT_MODE, Privatekey);
    	byte[] decipheredBytes;
		decipheredBytes = cipher.doFinal(bytes);
		return decipheredBytes;
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
    
    public static PrivateKey getPrivateKeyFromKeyStoreResource(String keyStoreResourcePath, char[] keyStorePassword,
			String keyAlias, char[] keyPassword)
			throws FileNotFoundException, KeyStoreException, UnrecoverableKeyException {
		KeyStore keystore = readKeystoreFromResource(keyStoreResourcePath, keyStorePassword);
		return getPrivateKeyFromKeyStore(keyAlias, keyPassword, keystore);
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
    
    public static KeyStore readKeystoreFromResource(String keyStoreResourcePath, char[] keyStorePassword)
			throws KeyStoreException {
		InputStream is = getResourceAsStream(keyStoreResourcePath);
		return readKeystoreFromStream(is, keyStorePassword);
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
 	
 	public static Certificate getX509CertificateFromResource(String certificateResourcePath)
			throws IOException, CertificateException {
		InputStream is = getResourceAsStream(certificateResourcePath);
		return getX509CertificateFromStream(is);
	}
 	
 	public static PublicKey getPublicKeyFromCertificate(Certificate certificate) {
		return certificate.getPublicKey();
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
 	  
 	

}

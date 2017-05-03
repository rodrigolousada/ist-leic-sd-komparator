package org.komparator.security;

import java.io.*;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import static javax.xml.bind.DatatypeConverter.printHexBinary;
import static javax.xml.bind.DatatypeConverter.printBase64Binary;


public class CryptoUtil {
	
	private static final String ASYM_CIPHER = "RSA/ECB/PKCS1Padding";
	

	
    private byte[] asymcipher(byte[] bytes, Key key){
    	
		try {
			
			Cipher cipher = Cipher.getInstance(ASYM_CIPHER);
			
			try {
				cipher.init(Cipher.ENCRYPT_MODE, key);
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			}
			
			try {
				byte [] cipheredBytes = cipher.doFinal(bytes);
				return cipheredBytes;
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    
    private byte[] asymdecipher(byte[] bytes, Key key){
    	
    	Cipher cipher;
		try {
			cipher = Cipher.getInstance(ASYM_CIPHER);
			
			try {
				cipher.init(Cipher.DECRYPT_MODE, key);
			} catch (InvalidKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	byte[] decipheredBytes;
			try {
				decipheredBytes = cipher.doFinal(bytes);
				return decipheredBytes;
			} catch (IllegalBlockSizeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
    }

}

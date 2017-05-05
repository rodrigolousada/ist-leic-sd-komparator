package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import org.junit.*;

import static org.junit.Assert.*;

public class CryptoUtilTest {
	
	private final String plainText = "This is the plain text!";
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();
	
	private String store_password = "1nsecure";
	private String key_password = "ins3cur3";
	
	private static final int ASYM_KEY_SIZE = 2048;
	private static final String ASYM_ALGO = "RSA";
	private static final String SIGNATURE_ALGO = "SHA256withRSA";


    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // runs once before all tests in the suite
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // initialization and clean-up for each test
    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void success() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	java.security.cert.Certificate certificate = CryptoUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CryptoUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = CryptoUtil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
		
		PrivateKey privatekey = CryptoUtil.getPrivateKeyFromKeyStoreResource("example.jks", store_password.toCharArray(), "example", key_password.toCharArray());
		
    	byte[] decipheredBytes = CryptoUtil.asymdecipher(cipheredBytes, privatekey);
    	assertNotNull(decipheredBytes);
    	
    	String decipheredText = new String(decipheredBytes);
    	
    	assertEquals(decipheredText, plainText);
    }
    
    @Test (expected=BadPaddingException.class)
    public void falseKeyTest() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	
    	java.security.cert.Certificate certificate = CryptoUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CryptoUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = CryptoUtil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
    	
    	KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
    	keyGen.initialize(ASYM_KEY_SIZE);
    	KeyPair keypair = keyGen.generateKeyPair();
    	
    	CryptoUtil.asymdecipher(cipheredBytes, keypair.getPrivate());
    }
    
    @Test(expected=BadPaddingException.class)
    public void TemperedByte() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	java.security.cert.Certificate certificate = CryptoUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CryptoUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = CryptoUtil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
		cipheredBytes[0]='j';
		
		PrivateKey privatekey = CryptoUtil.getPrivateKeyFromKeyStoreResource("example.jks", store_password.toCharArray(), "example", key_password.toCharArray());
		
    	CryptoUtil.asymdecipher(cipheredBytes, privatekey);
    }
    
    @Test
	public void testSignature() throws Exception {

		PrivateKey privateKey = CryptoUtil.getPrivateKeyFromKeyStoreResource("example.jks",
				store_password.toCharArray(), "example", key_password.toCharArray());
		byte[] digitalSignature = CryptoUtil.makeDigitalSignature(SIGNATURE_ALGO, privateKey, plainBytes);
		assertNotNull(digitalSignature);

		PublicKey publicKey = CryptoUtil.getX509CertificateFromResource("example.cer").getPublicKey();
		boolean result = CryptoUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes, digitalSignature);
		assertTrue(result);

		plainBytes[3] = 12;

		boolean resultAfterTamper = CryptoUtil.verifyDigitalSignature(SIGNATURE_ALGO, publicKey, plainBytes,
				digitalSignature);
		assertFalse(resultAfterTamper);
	}

}
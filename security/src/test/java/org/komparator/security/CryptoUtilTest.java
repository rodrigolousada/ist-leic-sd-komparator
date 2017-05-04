package org.komparator.security;

import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;

import javax.crypto.*;
import java.util.*;

import org.junit.*;
import static org.junit.Assert.*;

public class CryptoUtilTest {
	
	private final String plainText = "This is the plain text!";
	/** Plain text bytes. */
	private final byte[] plainBytes = plainText.getBytes();
	
	private static CryptoUtil cryptoutil;
	
	private String store_password = "1nsecure";
	private String key_password = "ins3cur3";
	
	private static final int ASYM_KEY_SIZE = 2048;
	private static final String ASYM_ALGO = "RSA";


    // one-time initialization and clean-up
    @BeforeClass
    public static void oneTimeSetUp() {
        // runs once before all tests in the suite
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // runs once after all tests in the suite
    }

    // members

    // initialization and clean-up for each test
    @Before
    public void setUp() {
    	cryptoutil= new CryptoUtil();
    }

    @After
    public void tearDown() {
        // runs after each test
    }

    // tests
    @Test
    public void success() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	java.security.cert.Certificate certificate = CertUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CertUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = cryptoutil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
		
		PrivateKey privatekey = CertUtil.getPrivateKeyFromKeyStoreResource("example.jks", store_password.toCharArray(), "example", key_password.toCharArray());
		
    	byte[] decipheredBytes = cryptoutil.asymdecipher(cipheredBytes, privatekey);
    	assertNotNull(decipheredBytes);
    	
    	String decipheredText = new String(decipheredBytes);
    	
    	assertEquals(decipheredText, plainText);
    	
    	//buscar certificado, buscar public key do certificado(.cer), encriptar cm public, sacar private do example, decriptar cm private, passar decriptados para string e comparar
    }
    
    @Test (expected=BadPaddingException.class)
    public void falseKeyTest() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	
    	java.security.cert.Certificate certificate = CertUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CertUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = cryptoutil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
    	
    	KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ASYM_ALGO);
    	keyGen.initialize(ASYM_KEY_SIZE);
    	KeyPair keypair = keyGen.generateKeyPair();
    	
    	byte[] decipheredBytes = cryptoutil.asymdecipher(cipheredBytes, keypair.getPrivate());
   
    	//publica igual, privado é falsa, mesmo processo com expcted error

    }
    
    @Test(expected=BadPaddingException.class)
    public void TemperedByte() throws CertificateException, IOException, NoSuchAlgorithmException, UnrecoverableKeyException, KeyStoreException, InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
    	java.security.cert.Certificate certificate = CertUtil.getX509CertificateFromResource("example.cer");
    	PublicKey publickey = CertUtil.getPublicKeyFromCertificate(certificate);
    	
		byte[] cipheredBytes = cryptoutil.asymcipher(plainBytes, publickey);
		assertNotNull(cipheredBytes);
		cipheredBytes[0]='j';
		
		PrivateKey privatekey = CertUtil.getPrivateKeyFromKeyStoreResource("example.jks", store_password.toCharArray(), "example", key_password.toCharArray());
		
    	byte[] decipheredBytes = cryptoutil.asymdecipher(cipheredBytes, privatekey);
    	
    	//igual ao success mas mudar um byte, text[0]='j'
    }

}
package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.*;

/**
 * Test suite
 */
public class BuyProductIT extends BaseIT {

	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		client.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			client.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			client.createProduct(product);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		client.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// tests
	// assertEquals(expected, actual);

	// public String buyProduct(String productId, int quantity)
	// throws BadProductId_Exception, BadQuantity_Exception,
	// InsufficientQuantity_Exception {

	// bad input tests

	//bad id
	@Test(expected = BadProductId_Exception.class)
	public void buyProductNullTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(null, 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductEmptyTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductWhitespaceTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct(" ", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductTabTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\t", 1);
	}

	@Test(expected = BadProductId_Exception.class)
	public void buyProductNewlineTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("\n", 1);
	}
	
	//bad quantity
	@Test(expected = BadQuantity_Exception.class)
	public void buyProductZeroQuantityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Z3", 0);
	}

	@Test(expected = BadQuantity_Exception.class)
	public void buyProductNegativeQuantityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Z3", -10);
	}

	// main tests
	@Test
	public void buyProductExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		String purchaseId = client.buyProduct("X1", 1);
		List<PurchaseView> listPurchases = client.listPurchases();
		PurchaseView purchase = listPurchases.get(0);
		ProductView product = client.getProduct(purchase.getProductId());
		
		assertEquals(purchaseId, purchase.getId());
		assertEquals("X1", product.getId());
		assertEquals(purchase.getProductId(), product.getId());
		assertEquals(10, product.getPrice());
		assertEquals(9, product.getQuantity());
		assertEquals("Basketball", product.getDesc());
	}
	
	@Test(expected = InsufficientQuantity_Exception.class)
	public void buyProductOverloadedQuantityTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("Z3", 100);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductNotExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("CVS", 1);
	}
	
	@Test(expected = BadProductId_Exception.class)
	public void buyProductLowercaseNotExistsTest() throws BadProductId_Exception, BadQuantity_Exception, InsufficientQuantity_Exception {
		client.buyProduct("x1", 1);
	}
}

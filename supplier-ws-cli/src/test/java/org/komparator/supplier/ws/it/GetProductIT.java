package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

/**
 * Test suite
 */
public class GetProductIT extends BaseIT {

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

	// public ProductView getProduct(String productId) throws
	// BadProductId_Exception {

	// bad input tests

	@Test(expected = BadProductId_Exception.class)
	public void getProductNullTest() throws BadProductId_Exception {
		client.getProduct(null);
	}

	@Test(expected = BadProductId_Exception.class)
	public void getProductEmptyTest() throws BadProductId_Exception {
		client.getProduct("");
	}

	@Test(expected = BadProductId_Exception.class)
	public void getProductWhitespaceTest() throws BadProductId_Exception {
		client.getProduct(" ");
	}

	@Test(expected = BadProductId_Exception.class)
	public void getProductTabTest() throws BadProductId_Exception {
		client.getProduct("\t");
	}

	@Test(expected = BadProductId_Exception.class)
	public void getProductNewlineTest() throws BadProductId_Exception {
		client.getProduct("\n");
	}

	// main tests

	@Test
	public void getProductExistsTest() throws BadProductId_Exception {
		ProductView product = client.getProduct("X1");
		assertEquals("X1", product.getId());
		assertEquals(10, product.getPrice());
		assertEquals(10, product.getQuantity());
		assertEquals("Basketball", product.getDesc());
	}

	@Test
	public void getProductAnotherExistsTest() throws BadProductId_Exception {
		ProductView product = client.getProduct("Y2");
		assertEquals("Y2", product.getId());
		assertEquals(20, product.getPrice());
		assertEquals(20, product.getQuantity());
		assertEquals("Baseball", product.getDesc());
	}

	@Test
	public void getProductYetAnotherExistsTest() throws BadProductId_Exception {
		ProductView product = client.getProduct("Z3");
		assertEquals("Z3", product.getId());
		assertEquals(30, product.getPrice());
		assertEquals(30, product.getQuantity());
		assertEquals("Soccer ball", product.getDesc());
	}

	@Test
	public void getProductNotExistsTest() throws BadProductId_Exception {
		// when product does not exist, null should be returned
		ProductView product = client.getProduct("A0");
		assertNull(product);
	}

	@Test
	public void getProductLowercaseNotExistsTest() throws BadProductId_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		ProductView product = client.getProduct("x1");
		assertNull(product);
	}

}

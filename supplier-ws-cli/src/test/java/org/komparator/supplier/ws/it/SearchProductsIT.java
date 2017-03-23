package org.komparator.supplier.ws.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

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
public class SearchProductsIT extends BaseIT {

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

	// public List<ProductView> searchProducts(String descText) throws
	// BadText_Exception

	// bad input tests
	
	@Test(expected = BadText_Exception.class)
	public void searchProductsNullTest() throws BadText_Exception, BadProductId_Exception {
		client.searchProducts(null);
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsEmptyTest() throws BadText_Exception, BadProductId_Exception {
		client.searchProducts("");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsWhitespaceTest() throws BadText_Exception, BadProductId_Exception {
		client.searchProducts(" ");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsTabTest() throws BadText_Exception, BadProductId_Exception {
		client.searchProducts("\t");
	}

	@Test(expected = BadText_Exception.class)
	public void searchProductsNewlineTest() throws BadText_Exception, BadProductId_Exception {
		client.searchProducts("\n");
	}

	
	// main tests

	@Test
	public void searchProductsExistsTest() throws BadText_Exception, BadProductId_Exception {
		List<ProductView> foundProducts = client.searchProducts("Basketball");
		assertEquals(1, foundProducts.size());
		assertEquals("X1", foundProducts.get(0).getId());
		assertEquals(10, foundProducts.get(0).getPrice());
		assertEquals(10, foundProducts.get(0).getQuantity());
		assertEquals("Basketball", foundProducts.get(0).getDesc());
	}

	@Test
	public void searchProductsAnotherExistsTest() throws BadText_Exception, BadProductId_Exception {
		List<ProductView> foundProducts = client.searchProducts("Bas");
		assertEquals(2, foundProducts.size());
		assertEquals("X1", foundProducts.get(0).getId());
		assertEquals(10, foundProducts.get(0).getPrice());
		assertEquals(10, foundProducts.get(0).getQuantity());
		assertEquals("Basketball", foundProducts.get(0).getDesc());
		assertEquals("Y2", foundProducts.get(1).getId());
		assertEquals(20, foundProducts.get(1).getPrice());
		assertEquals(20, foundProducts.get(1).getQuantity());
		assertEquals("Baseball", foundProducts.get(1).getDesc());
	}

	@Test
	public void searchProductsYetAnotherExistsTest() throws BadText_Exception, BadProductId_Exception {
		List<ProductView> foundProducts = client.searchProducts("ball");
		assertEquals(3, foundProducts.size());
		assertEquals("X1", foundProducts.get(0).getId());
		assertEquals(10, foundProducts.get(0).getPrice());
		assertEquals(10, foundProducts.get(0).getQuantity());
		assertEquals("Basketball", foundProducts.get(0).getDesc());
		assertEquals("Y2", foundProducts.get(1).getId());
		assertEquals(20, foundProducts.get(1).getPrice());
		assertEquals(20, foundProducts.get(1).getQuantity());
		assertEquals("Baseball", foundProducts.get(1).getDesc());
		assertEquals("Z3", foundProducts.get(2).getId());
		assertEquals(30, foundProducts.get(2).getPrice());
		assertEquals(30, foundProducts.get(2).getQuantity());
		assertEquals("Soccer ball", foundProducts.get(2).getDesc());
	}

	@Test
	public void searchProductsNotExistsTest() throws BadText_Exception, BadProductId_Exception {
		// when product does not exist, null should be returned
		List<ProductView> foundProducts = client.searchProducts("CVS");
		assertEquals(0, foundProducts.size());
	}

	@Test
	public void searchProductsLowercaseNotExistsTest() throws BadText_Exception, BadProductId_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		List<ProductView> foundProducts = client.searchProducts("basketball");
		assertEquals(0, foundProducts.size());
	}

}

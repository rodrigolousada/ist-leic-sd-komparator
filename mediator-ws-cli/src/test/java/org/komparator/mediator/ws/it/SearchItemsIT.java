package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class SearchItemsIT extends BaseIT {
	// static members

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() throws BadProductId_Exception, BadProduct_Exception {
		// clear remote service state before all tests
		mediatorClient.clear();

		// fill-in test products
		// (since getProduct is read-only the initialization below
		// can be done once for all tests in this suite)
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(15);
			product.setQuantity(15);
			supplierClient1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Y2");
			product.setDesc("Baseball");
			product.setPrice(20);
			product.setQuantity(20);
			supplierClient1.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("X1");
			product.setDesc("Basketball");
			product.setPrice(10);
			product.setQuantity(10);
			supplierClient2.createProduct(product);
		}
		{
			ProductView product = new ProductView();
			product.setId("Z3");
			product.setDesc("Soccer ball");
			product.setPrice(30);
			product.setQuantity(30);
			supplierClient2.createProduct(product);
		}
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
	}

	// bad input tests

	@Test(expected = InvalidText_Exception.class)
	public void getProductNullTest() throws InvalidText_Exception {
		mediatorClient.searchItems(null);
	}

	@Test(expected = InvalidText_Exception.class)
	public void getProductEmptyTest() throws InvalidText_Exception {
		mediatorClient.searchItems("");
	}

	@Test(expected = InvalidText_Exception.class)
	public void getProductWhitespaceTest() throws InvalidText_Exception {
		mediatorClient.searchItems(" ");
	}

	@Test(expected = InvalidText_Exception.class)
	public void getProductTabTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\t");
	}

	@Test(expected = InvalidText_Exception.class)
	public void getProductNewlineTest() throws InvalidText_Exception {
		mediatorClient.searchItems("\n");
	}

	// main tests

	@Test
	public void getProductExistsTest() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("ball");
		assertEquals(4, items.size());
		assertEquals("X1", items.get(0).getItemId().getProductId());
		assertEquals(10, items.get(0).getPrice());
		assertEquals("Basketball", items.get(0).getDesc());
		assertEquals("X1", items.get(1).getItemId().getProductId());
		assertEquals(15, items.get(1).getPrice());
		assertEquals("Basketball", items.get(1).getDesc());
		assertEquals("Y2", items.get(2).getItemId().getProductId());
		assertEquals(20, items.get(2).getPrice());
		assertEquals("Baseball", items.get(2).getDesc());
		assertEquals("Z3", items.get(3).getItemId().getProductId());
		assertEquals(30, items.get(3).getPrice());
		assertEquals("Soccer ball", items.get(3).getDesc());
	}

	@Test
	public void getProductAnotherExistsTest() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("Basketball");
		assertEquals(2, items.size());
		assertEquals("X1", items.get(0).getItemId().getProductId());
		assertEquals(10, items.get(0).getPrice());
		assertEquals("Basketball", items.get(0).getDesc());
		assertEquals("X1", items.get(1).getItemId().getProductId());
		assertEquals(15, items.get(1).getPrice());
		assertEquals("Basketball", items.get(1).getDesc());

	}

	@Test
	public void getProductYetAnotherExistsTest() throws InvalidText_Exception {
		List<ItemView> items = mediatorClient.searchItems("Soccer ball");
		assertEquals(1, items.size());
		assertEquals("Z3", items.get(0).getItemId().getProductId());
		assertEquals(30, items.get(0).getPrice());
		assertEquals("Soccer ball", items.get(0).getDesc());
	}

	@Test
	public void getProductNotExistsTest() throws InvalidText_Exception {
		// when product does not exist, null should be returned
		List<ItemView> items = mediatorClient.searchItems("CVS");
		assertEquals(0, items.size());
	}

	@Test
	public void getProductLowercaseNotExistsTest() throws InvalidText_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		List<ItemView> items = mediatorClient.searchItems("basketball");
		assertEquals(0, items.size());
	}
}


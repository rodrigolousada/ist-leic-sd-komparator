package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.ItemView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class GetItemIT extends BaseIT {
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

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNullTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(null);
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsEmptyTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsWhitespaceTest() throws InvalidItemId_Exception {
		mediatorClient.getItems(" ");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsTabTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\t");
	}

	@Test(expected = InvalidItemId_Exception.class)
	public void getItemsNewlineTest() throws InvalidItemId_Exception {
		mediatorClient.getItems("\n");
	}

	// main tests

	@Test
	public void getItemsExistsTest() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("X1");
		assertEquals(2, items.size());
		assertEquals("X1", items.get(0).getItemId().getProductId());
		assertEquals(10, items.get(0).getPrice());
		assertEquals(15, items.get(1).getPrice());
		assertEquals("Basketball", items.get(0).getDesc());
	}

	@Test
	public void getItemsAnotherExistsTest() throws InvalidItemId_Exception  {
		List<ItemView> items = mediatorClient.getItems("Y2");
		assertEquals(1, items.size());
		assertEquals("Y2", items.get(0).getItemId().getProductId());
		assertEquals(20, items.get(0).getPrice());
		assertEquals("Baseball", items.get(0).getDesc());
	}

	@Test
	public void getItemsYetAnotherExistsTest() throws InvalidItemId_Exception {
		List<ItemView> items = mediatorClient.getItems("Z3");
		assertEquals(1, items.size());
		assertEquals("Z3", items.get(0).getItemId().getProductId());
		assertEquals(30, items.get(0).getPrice());
		assertEquals("Soccer ball", items.get(0).getDesc());
	}

	@Test
	public void getItemsNotExistsTest() throws InvalidItemId_Exception {
		// when product does not exist, null should be returned
		List<ItemView> items = mediatorClient.getItems("A0");
		assertEquals(0, items.size());
	}

	@Test
	public void getItemsLowercaseNotExistsTest() throws InvalidItemId_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		List<ItemView> items = mediatorClient.getItems("x1");
		assertEquals(0, items.size());
	}
}

package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

import junit.framework.Assert;

public class AddToCartIT extends BaseIT {
	// static members
	ItemIdView itemId = null;

	// one-time initialization and clean-up
	@BeforeClass
	public static void oneTimeSetUp() {
		// clear remote service state before all tests
		mediatorClient.clear();
	}

	@AfterClass
	public static void oneTimeTearDown() {
		// clear remote service state after all tests
		mediatorClient.clear();
	}

	// members

	// initialization and clean-up for each test
	@Before
	public void setUp() throws BadProductId_Exception, BadProduct_Exception {
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
		this.itemId = new ItemIdView();
		this.itemId.setProductId(supplierClient1.getProduct("Y2").getId());
		this.itemId.setSupplierId(supplierClient1.getSupplierId());
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
	}

	// bad input tests

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartNullCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart(null, itemId, 1);
	}
	
	@Test(expected = InvalidItemId_Exception.class)
	public void addToCartNullIteamIdTest() throws InvalidItemId_Exception, InvalidCartId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", null, 1);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
	public void addToCartNegativeQuantityTest() throws InvalidQuantity_Exception, InvalidCartId_Exception, InvalidItemId_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, -1);
	}
	
	@Test(expected = InvalidQuantity_Exception.class)
	public void addToCartZeroQuantityTest() throws InvalidQuantity_Exception, InvalidCartId_Exception, InvalidItemId_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, 0);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartEmptyCartIdTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("", itemId, 1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartWhitespaceTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("  ", itemId, 1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartTabTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\t", itemId, 1);
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void addToCartNewlineTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("\n", itemId, 1);
	}

	// main tests

	@Test
	public void addToCartExistsTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, 1);
		
		assertEquals(1, mediatorClient.listCarts().size());
		CartView cart = mediatorClient.listCarts().get(0);
		assertEquals(cart.getCartId(), "cart");
		assertEquals(cart.getItems().get(0).getItem().getItemId().getProductId(), itemId.getProductId());
		assertEquals(cart.getItems().get(0).getItem().getItemId().getSupplierId(), itemId.getSupplierId());
		assertEquals(1, cart.getItems().get(0).getQuantity());
	}
	
	@Test
	public void addToCartAnotherExistsTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, BadProductId_Exception {
		this.itemId.setProductId(supplierClient1.getProduct("X1").getId());
		this.itemId.setSupplierId(supplierClient1.getSupplierId());
		
		ItemIdView itemId2 = new ItemIdView();
		itemId2.setProductId(supplierClient2.getProduct("X1").getId());
		itemId2.setSupplierId(supplierClient2.getSupplierId());
		mediatorClient.addToCart("cart", itemId, 5);
		mediatorClient.addToCart("cart", itemId2, 7);
		
		assertEquals(1, mediatorClient.listCarts().size());
		CartView cart = mediatorClient.listCarts().get(0);
		assertEquals(cart.getCartId(), "cart");
		
		assertEquals(2, cart.getItems().size());
		assertEquals(cart.getItems().get(0).getItem().getItemId().getProductId(), itemId.getProductId());
		assertEquals(cart.getItems().get(0).getItem().getItemId().getSupplierId(), itemId.getSupplierId());
		assertEquals(5, cart.getItems().get(0).getQuantity());
		
		assertEquals(cart.getItems().get(1).getItem().getItemId().getProductId(), itemId2.getProductId());
		assertEquals(cart.getItems().get(1).getItem().getItemId().getSupplierId(), itemId2.getSupplierId());
		assertEquals(7, cart.getItems().get(1).getQuantity());
	}
	
	@Test
	public void addToCartMaxLimitTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, 20);
		
		assertEquals(1, mediatorClient.listCarts().size());
		CartView cart = mediatorClient.listCarts().get(0);
		assertEquals(cart.getCartId(), "cart");
		assertEquals(cart.getItems().get(0).getItem().getItemId().getProductId(), itemId.getProductId());
		assertEquals(cart.getItems().get(0).getItem().getItemId().getSupplierId(), itemId.getSupplierId());
		assertEquals(20, cart.getItems().get(0).getQuantity());
	}

	@Test (expected = NotEnoughItems_Exception.class)
	public void addToCartNotEnoughItemsTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, 21);
	}

	@Test (expected = NotEnoughItems_Exception.class)
	public void addToCartAnotherNotEnoughItemsTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		mediatorClient.addToCart("cart", itemId, 19);
		mediatorClient.addToCart("cart", itemId, 2);
	}

	@Test (expected = InvalidItemId_Exception.class)
	public void addToCartNotExistsTest() throws InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception, BadProductId_Exception {
		// when product does not exist, null should be returned
		ItemIdView invalidItemId = new ItemIdView();
		invalidItemId.setProductId("YY");
		invalidItemId.setSupplierId(supplierClient1.getSupplierId());
		mediatorClient.addToCart("cart", invalidItemId, 1);
	}
	
	@Test
	public void addToCartLowercaseNotExistsTest() throws BadProductId_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		mediatorClient.addToCart("cart", itemId, 5);
		mediatorClient.addToCart("Cart", itemId, 5);
		assertEquals(2, mediatorClient.listCarts().size());
		CartView cart1 = mediatorClient.listCarts().get(0);
		CartView cart2 = mediatorClient.listCarts().get(1);
		assertEquals(cart1.getCartId(), "cart");
		assertEquals(cart1.getItems().get(0).getItem().getItemId().getProductId(), itemId.getProductId());
		assertEquals(cart1.getItems().get(0).getItem().getItemId().getSupplierId(), itemId.getSupplierId());
		assertEquals(5, cart1.getItems().get(0).getQuantity());
		assertEquals(cart2.getCartId(), "Cart");
		assertEquals(cart2.getItems().get(0).getItem().getItemId().getProductId(), itemId.getProductId());
		assertEquals(cart2.getItems().get(0).getItem().getItemId().getSupplierId(), itemId.getSupplierId());
		assertEquals(5, cart2.getItems().get(0).getQuantity());
	}

	@Test (expected = InvalidItemId_Exception.class)
	public void addToCartLowercaseNotExistsExceptionTest() throws BadProductId_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		ItemIdView invalidItemId = new ItemIdView();
		invalidItemId.setProductId("x1");
		invalidItemId.setSupplierId(supplierClient1.getSupplierId());
		mediatorClient.addToCart("cart", invalidItemId, 1);
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void addToCartNullSupplierTest() throws BadProductId_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		ItemIdView invalidItemId = new ItemIdView();
		invalidItemId.setProductId("x1");
		invalidItemId.setSupplierId(null);
		mediatorClient.addToCart("cart", invalidItemId, 1);
	}
	
	@Test (expected = InvalidItemId_Exception.class)
	public void addToCartWrongSupplierTest() throws BadProductId_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// product identifiers are case sensitive,
		// so "x1" is not the same as "X1"
		ItemIdView invalidItemId = new ItemIdView();
		invalidItemId.setProductId("x1");
		invalidItemId.setSupplierId("Nao Existo");
		mediatorClient.addToCart("cart", invalidItemId, 1);
	}
}

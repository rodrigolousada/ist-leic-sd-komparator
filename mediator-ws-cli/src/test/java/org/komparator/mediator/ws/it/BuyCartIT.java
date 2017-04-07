package org.komparator.mediator.ws.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.komparator.mediator.ws.CartView;
import org.komparator.mediator.ws.EmptyCart_Exception;
import org.komparator.mediator.ws.InvalidCartId_Exception;
import org.komparator.mediator.ws.InvalidCreditCard_Exception;
import org.komparator.mediator.ws.InvalidItemId_Exception;
import org.komparator.mediator.ws.InvalidQuantity_Exception;
import org.komparator.mediator.ws.InvalidText_Exception;
import org.komparator.mediator.ws.ItemIdView;
import org.komparator.mediator.ws.ItemView;
import org.komparator.mediator.ws.NotEnoughItems_Exception;
import org.komparator.mediator.ws.Result;
import org.komparator.mediator.ws.ShoppingResultView;
import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.BadProduct_Exception;
import org.komparator.supplier.ws.ProductView;

public class BuyCartIT extends BaseIT {
	// static members
	ItemIdView itemId1 = null;
	ItemIdView itemId2 = null;
	ItemIdView itemId3 = null;

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
	public void setUp() throws BadProductId_Exception, BadProduct_Exception, InvalidCartId_Exception, InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
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
		
		this.itemId1 = new ItemIdView();
		this.itemId1.setProductId(supplierClient1.getProduct("Y2").getId());
		this.itemId1.setSupplierId(supplierClient1.getSupplierId());
		mediatorClient.addToCart("someCart", itemId1, 10);
		
		this.itemId2 = new ItemIdView();
		this.itemId2.setProductId(supplierClient1.getProduct("X1").getId());
		this.itemId2.setSupplierId(supplierClient1.getSupplierId());
		mediatorClient.addToCart("cart", itemId2, 10);
		
		this.itemId3 = new ItemIdView();
		this.itemId3.setProductId(supplierClient2.getProduct("X1").getId());
		this.itemId3.setSupplierId(supplierClient2.getSupplierId());
		mediatorClient.addToCart("cart", itemId3, 10);
		
		mediatorClient.addToCart("partialCart", itemId1, 15);
		mediatorClient.addToCart("partialCart", itemId2, 10);
		
		mediatorClient.addToCart("emptyCart", itemId1, 15);
	}

	@After
	public void tearDown() {
		mediatorClient.clear();
	}

	// bad input tests

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCartIdNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(null, "12345");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCardIdEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("", "12345");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCardIdWhitespaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart(" ", "12345");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCardIdTabTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\t", "12345");
	}

	@Test(expected = InvalidCartId_Exception.class)
	public void buyCartCardIdNewlineTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("\n", "12345");
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartCreditCardNrNullTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("cart", null);
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartCreditCardNrEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("cart", "");
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartCreditCardNrWhitespaceTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("cart", " ");
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartCreditCardNrTabTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("cart", "\t");
	}

	@Test(expected = InvalidCreditCard_Exception.class)
	public void buyCartCreditCardNrNewlineTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("cart", "\n");
	}
	
	// main tests
	@Test
	public void buyCartCompleteTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception  {
		ShoppingResultView shoppingresult = mediatorClient.buyCart("someCart", "4024007102923926");
		
		assertEquals(1, shoppingresult.getPurchasedItems().size());
		assertEquals(0, shoppingresult.getDroppedItems().size());
		assertEquals(shoppingresult.getResult(), Result.COMPLETE);
		assertEquals(200, shoppingresult.getTotalPrice());
		assertEquals(20, shoppingresult.getPurchasedItems().get(0).getItem().getPrice());
	}
	
	@Test
	public void buyCartPartialTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception  {
		ShoppingResultView shoppingresult1 = mediatorClient.buyCart("someCart", "4024007102923926");
		ShoppingResultView shoppingresult2 = mediatorClient.buyCart("partialCart", "4024007102923926");
		
		assertEquals(1, shoppingresult1.getPurchasedItems().size());
		assertEquals(0, shoppingresult1.getDroppedItems().size());
		assertEquals(shoppingresult1.getResult(), Result.COMPLETE);
		assertEquals(200, shoppingresult1.getTotalPrice());
		assertEquals(20, shoppingresult1.getPurchasedItems().get(0).getItem().getPrice());
		
		assertEquals(1, shoppingresult2.getPurchasedItems().size());
		assertEquals(1, shoppingresult2.getDroppedItems().size());
		assertEquals(shoppingresult2.getResult(), Result.PARTIAL);
		assertEquals(150, shoppingresult2.getTotalPrice());
		assertEquals(15, shoppingresult2.getPurchasedItems().get(0).getItem().getPrice());
	}
	
	@Test
	public void buyCartEmptyTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception  {
		ShoppingResultView shoppingresult1 = mediatorClient.buyCart("someCart", "4024007102923926");
		ShoppingResultView shoppingresult2 = mediatorClient.buyCart("emptyCart", "4024007102923926");
		
		assertEquals(1, shoppingresult1.getPurchasedItems().size());
		assertEquals(0, shoppingresult1.getDroppedItems().size());
		assertEquals(shoppingresult1.getResult(), Result.COMPLETE);
		assertEquals(200, shoppingresult1.getTotalPrice());
		assertEquals(20, shoppingresult1.getPurchasedItems().get(0).getItem().getPrice());
		
		assertEquals(0, shoppingresult2.getPurchasedItems().size());
		assertEquals(1, shoppingresult2.getDroppedItems().size());
		assertEquals(shoppingresult2.getResult(), Result.EMPTY);
	}
	
	@Test (expected = InvalidCartId_Exception.class)
	public void buyCartNotExistsTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		mediatorClient.buyCart("The XX", "4024007102923926");
	}
	
	@Test (expected = InvalidCartId_Exception.class)
	public void buyCartLowercaseNotExistsExceptionTest() throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		// case sensitive,
		// so "cart" is not the same as "Cart"
		mediatorClient.buyCart("Cart", "4024007102923926");
	}
}

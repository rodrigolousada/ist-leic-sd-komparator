package org.komparator.mediator.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.komparator.supplier.ws.BadProductId_Exception;
import org.komparator.supplier.ws.ProductView;
import org.komparator.supplier.ws.cli.SupplierClient;
import org.komparator.supplier.ws.cli.SupplierClientException;

import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINaming;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDINamingException;
import pt.ulisboa.tecnico.sdis.ws.uddi.UDDIRecord;

@WebService(endpointInterface = "org.komparator.mediator.ws.MediatorPortType", wsdlLocation = "mediator.wsdl", name = "MediatorWebService", portName = "MediatorPort", targetNamespace = "http://ws.mediator.komparator.org/", serviceName = "MediatorService")

public class MediatorPortImpl implements MediatorPortType {

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}

	private List<CartView> carts = new ArrayList<CartView>();
	// Main operations -------------------------------------------------------

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		// check product id
		if (productId == null)
			throwInvalidItemId("Product identifier cannot be null!");
		productId = productId.trim();
		if (productId.length() == 0)
			throwInvalidItemId("Product identifier cannot be empty or whitespace!");

		List<ItemView> itemlist = new ArrayList<ItemView>();
		List<SupplierClient> clients = getAllSuppliers();

		for (SupplierClient client : clients) {
			try {
				ProductView product = client.getProduct(productId);
				if (product != null) {
					ItemView item = newItemView(product, client);
					itemlist.add(item);
				}

			} catch (BadProductId_Exception e) {
				System.out.println("No such product available");
				e.printStackTrace();
			}
		}

		Collections.sort(itemlist, new Comparator<ItemView>() {
			@Override
			public int compare(ItemView item1, ItemView item2) {
				return item1.getPrice() - item2.getPrice();
			}
		});
		return itemlist;
	}

	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		if (descText == null) {
			throwInvalidText("Search Items: incorrect argument");
		}
		descText = descText.trim();
		if (descText.length() == 0) {
			throwInvalidText("Search Items: incorrect argument");
		}

		List<ItemView> foundItems = new ArrayList<ItemView>();
		List<SupplierClient> clients = getAllSuppliers();

		for (SupplierClient client : clients) {
			for (ProductView element : client.listProducts()) {
				if (element.getDesc().contains(descText)) {
					ItemView item = newItemView(element, client);
					foundItems.add(item);
				}
			}
		}

		Collections.sort(foundItems, new Comparator<ItemView>() {
			@Override
			public int compare(ItemView item1, ItemView item2) {
				String id1 = item1.getItemId().getProductId();
				String id2 = item2.getItemId().getProductId();
				int resultComp = id1.compareTo(id2);
				if (resultComp == 0) {
					Integer int1 = item1.getPrice();
					Integer int2 = item2.getPrice();
					resultComp = int1.compareTo(int2);
				}
				return resultComp;
			}
		});

		return foundItems;
	}

	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		if (cartId == null) {
			throwInvalidCartId("CartId: incorrect argument");
		}
		if(itemId == null){
			throwInvalidItemId("itemId: incorrect argument");
		}
		if (itemId.getProductId() == null) {
			throwInvalidItemId("ProductId: incorrect argument");
		}
		if (itemId.getSupplierId() == null) {
			throwInvalidItemId("SupplierId: incorrect argument");
		}

		cartId = cartId.trim();
		itemId.setProductId(itemId.getProductId().trim());
		itemId.setSupplierId(itemId.getSupplierId().trim());

		if (cartId.length() == 0) {
			throwInvalidCartId("CartId: incorrect argument");
		}
		if (itemId.getProductId().length() == 0) {
			throwInvalidItemId("ProductId: incorrect argument");
		}
		if (itemId.getSupplierId().length() == 0) {
			throwInvalidItemId("SupplierId: incorrect argument");
		}

		if (itemQty < 1) {
			throwInvalidQuantity("ItemQuantity: incorrect argument");
		}

		List<SupplierClient> clients = getAllSuppliers();
		
		for (SupplierClient client : clients) {
			if (client.getSupplierId().equals(itemId.getSupplierId())){
				for (ProductView product : client.listProducts()) {
					if (product.getId().equals(itemId.getProductId())) {
						if (product.getQuantity() >= itemQty) {
							for (CartView cart : listCarts()) {
								if (cart.getCartId().equals(cartId)) {
									for (CartItemView cartItem : cart.getItems()) {
										if (cartItem.getItem().getItemId().equals(itemId)) {
											cartItem.setQuantity(cartItem.getQuantity() + itemQty);
										}
									}
									CartItemView newcartItem = newCartItem(product, client, itemQty);
									cart.getItems().add(newcartItem);
								}
							}
							CartView newcart = new CartView();
							CartItemView newcartItem = newCartItem(product, client, itemQty);
							newcart.getItems().add(newcartItem);
							listCarts().add(newcart);
						}
						throwNotEnoughItems("Supplier doesn't have enough items");
					}
				}
				throwInvalidItemId("Supplier doesn't have the item");
			}
		}
		throwInvalidItemId("Supplier doesn't exist");
	}

	@Override
	public ShoppingResultView buyCart(String cartId, String creditCardNr)
			throws EmptyCart_Exception, InvalidCartId_Exception, InvalidCreditCard_Exception {
		// TODO Auto-generated method stub
		return null;
	}

	// Auxiliary operations --------------------------------------------------

	@Override
	public String ping(String arg0) {
		if (arg0 == null || arg0.trim().length() == 0)
			arg0 = "friend";

		String wsName = "Mediator";

		StringBuilder builder = new StringBuilder();
		builder.append("Hello ").append(arg0);
		builder.append(" from ").append(wsName);
		List<SupplierClient> clients = getAllSuppliers();
		if (clients != null) {
			for (SupplierClient client : clients) {
				builder.append(client.ping(arg0)).append("\n");
			}
		}
		return builder.toString();
	}

	private List<SupplierClient> getAllSuppliers() {
		List<SupplierClient> suppliers = new ArrayList<SupplierClient>();
		UDDINaming uddinaming = endpointManager.getUddiNaming();
		Collection<UDDIRecord> records = null;
		try {
			records = uddinaming.listRecords("A63_Supplier%");
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}
		for (UDDIRecord record : records) {
			SupplierClient client;
			try {
				client = new SupplierClient(record.getUrl());
				client.setWsName(record.getOrgName());
				suppliers.add(client);
			} catch (SupplierClientException e) {
				e.printStackTrace();
			}
		}
		return suppliers;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<CartView> listCarts() {
		return carts;
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		// TODO Auto-generated method stub
		return null;
	}

	// View helpers -----------------------------------------------------

	private ItemIdView newItemIdView(ProductView product, SupplierClient client) {
		ItemIdView itemId = new ItemIdView();
		itemId.setProductId(product.getId());
		itemId.setSupplierId(client.getSupplierId());
		return itemId;
	}

	private ItemView newItemView(ProductView product, SupplierClient client) {
		ItemView item = new ItemView();
		ItemIdView itemId = newItemIdView(product, client);
		item.setDesc(product.getDesc());
		item.setPrice(product.getPrice());
		item.setItemId(itemId);
		return item;
	}

	private CartItemView newCartItem(ProductView product, SupplierClient client, int itemQty) {
		CartItemView newcartItem = new CartItemView();
		ItemView item = newItemView(product, client);
		newcartItem.setItem(item);
		newcartItem.setQuantity(itemQty);
		return newcartItem;
	}

	// Exception helpers -----------------------------------------------------

	// Helper method to throw new InvalidItemId exception
	private void throwInvalidItemId(final String message) throws InvalidItemId_Exception {
		InvalidItemId faultInfo = new InvalidItemId();
		faultInfo.message = message;
		throw new InvalidItemId_Exception(message, faultInfo);
	}

	// Helper method to throw new InvalidText exception
	private void throwInvalidText(final String message) throws InvalidText_Exception {
		InvalidText faultInfo = new InvalidText();
		faultInfo.message = message;
		throw new InvalidText_Exception(message, faultInfo);
	}

	// Helper method to throw new InvalidCartId exception
	private void throwInvalidCartId(final String message) throws InvalidCartId_Exception {
		InvalidCartId faultInfo = new InvalidCartId();
		faultInfo.message = message;
		throw new InvalidCartId_Exception(message, faultInfo);
	}

	// Helper method to throw new InvalidQuantity exception
	private void throwInvalidQuantity(final String message) throws InvalidQuantity_Exception {
		InvalidQuantity faultInfo = new InvalidQuantity();
		faultInfo.message = message;
		throw new InvalidQuantity_Exception(message, faultInfo);
	}

	// Helper method to throw new NotEnoughItems exception
	private void throwNotEnoughItems(final String message) throws NotEnoughItems_Exception {
		NotEnoughItems faultInfo = new NotEnoughItems();
		faultInfo.message = message;
		throw new NotEnoughItems_Exception(message, faultInfo);
	}

	// Helper method to throw new EmptyCart exception
	private void throwEmptyCart(final String message) throws EmptyCart_Exception {
		EmptyCart faultInfo = new EmptyCart();
		faultInfo.message = message;
		throw new EmptyCart_Exception(message, faultInfo);
	}

	// Helper method to throw new InvalidCreditCard exception
	private void throwInvalidCreditCard(final String message) throws InvalidCreditCard_Exception {
		InvalidCreditCard faultInfo = new InvalidCreditCard();
		faultInfo.message = message;
		throw new InvalidCreditCard_Exception(message, faultInfo);
	}
}

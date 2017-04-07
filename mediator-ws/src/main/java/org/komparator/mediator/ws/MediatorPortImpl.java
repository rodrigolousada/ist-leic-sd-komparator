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

@WebService(
		endpointInterface = "org.komparator.mediator.ws.MediatorPortType", 
		wsdlLocation = "mediator.wsdl", 
		name = "MediatorWebService", 
		portName = "MediatorPort", 
		targetNamespace = "http://ws.mediator.komparator.org/", 
		serviceName = "MediatorService"
)

public class MediatorPortImpl implements MediatorPortType {

	// end point manager
	private MediatorEndpointManager endpointManager;

	public MediatorPortImpl(MediatorEndpointManager endpointManager) {
		this.endpointManager = endpointManager;
	}
	
	private ListCartsResponse listcarts;
	// Main operations -------------------------------------------------------

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		// check product id
		if (productId == null)
			throwInvalidItemId("Product identifier cannot be null!");
		productId = productId.trim();
		if (productId.length() == 0)
			throwInvalidItemId("Product identifier cannot be empty or whitespace!");
		
		List<ItemView> itemlist= new ArrayList<ItemView>();
		List<SupplierClient> clients = getAllSuppliers();
			
		for(SupplierClient client : clients){
			try {
				ProductView product = client.getProduct(productId);
				if(product !=null){
					ItemView item = newItemView(product, client);
					itemlist.add(item);
				}
				
			} catch (BadProductId_Exception e) {
				System.out.println("No product available");
				e.printStackTrace();
			}	
		}
			
		Collections.sort(itemlist, new Comparator<ItemView>() {
			@Override
			public int compare(ItemView item1, ItemView item2){
				return item1.getPrice() - item2.getPrice();
			}
		});		
		return itemlist;
	}
	
	@Override
	public List<ItemView> searchItems(String descText) throws InvalidText_Exception {
		if(descText == null){
			throwInvalidText("Search Items: incorrect argument");
		}
		descText=descText.trim();
		if(descText.length() == 0){
			throwInvalidText("Search Items: incorrect argument");
		}
		
		List<ItemView> foundItems = new ArrayList<ItemView>();
		List<SupplierClient> clients = getAllSuppliers();
		
		for(SupplierClient client : clients){
			for(ProductView element : client.listProducts()) {
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
	            	resultComp=int1.compareTo(int2);
	           }
               return resultComp;
	    }});
		
		return foundItems;
	}
	
	@Override
	public void addToCart(String cartId, ItemIdView itemId, int itemQty) throws InvalidCartId_Exception,
			InvalidItemId_Exception, InvalidQuantity_Exception, NotEnoughItems_Exception {
		// TODO Auto-generated method stub
		
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
		List<SupplierClient> clients = getAllSuppliers();
		if(clients != null) {
			for(SupplierClient client : clients) {
				builder.append(client.ping(arg0)).append("\n");
			}
		}
		return builder.toString();
	}

	public List<SupplierClient> getAllSuppliers() {
		List<SupplierClient> suppliers = new ArrayList<SupplierClient>();
		UDDINaming uddinaming = endpointManager.getUddiNaming();
		Collection<UDDIRecord> records = null;
		try {
			records = uddinaming.listRecords("A63_Supplier%");
		} catch (UDDINamingException e) {
			e.printStackTrace();
		}
		for(UDDIRecord record : records) {
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ShoppingResultView> shopHistory() {
		// TODO Auto-generated method stub
		return null;
	}
	
	// View helpers -----------------------------------------------------
	
    /*
    private ProductView newProductView(Product product) {
		ProductView view = new ProductView();
		view.setId(product.getId());
		view.setDesc(product.getDescription());
		view.setQuantity(product.getQuantity());
		view.setPrice(product.getPrice());
		return view;
	}
	 

	private PurchaseView newPurchaseView(Purchase purchase) {
		PurchaseView view = new PurchaseView();
		view.setId(purchase.getPurchaseId());
		view.setProductId(purchase.getProductId());
		view.setQuantity(purchase.getQuantity());
		view.setUnitPrice(purchase.getUnitPrice());
		return view;
	}
	     * */
	private ItemView newItemView(ProductView product, SupplierClient client){
		ItemView item = new ItemView();
		ItemIdView aux_id = new ItemIdView();
		item.setDesc(product.getDesc());
		item.setPrice(product.getPrice());
		aux_id.setProductId(product.getId());
		aux_id.supplierId= client.getSupplierId();
		item.setItemId(aux_id);
		return item;
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
/*
	// Helper method to throw new BadProduct exception
	private void throwBadProduct(final String message) throws BadProduct_Exception {
		BadProduct faultInfo = new BadProduct();
		faultInfo.message = message;
		throw new BadProduct_Exception(message, faultInfo);
	}

	// Helper method to throw new BadQuantity exception
	private void throwBadQuantity(final String message) throws BadQuantity_Exception {
		BadQuantity faultInfo = new BadQuantity();
		faultInfo.message = message;
		throw new BadQuantity_Exception(message, faultInfo);
	}

	// Helper method to throw new InsufficientQuantity exception
	private void throwInsufficientQuantity(final String message) throws InsufficientQuantity_Exception {
		InsufficientQuantity faultInfo = new InsufficientQuantity();
		faultInfo.message = message;
		throw new InsufficientQuantity_Exception(message, faultInfo);
	}
	*/

}

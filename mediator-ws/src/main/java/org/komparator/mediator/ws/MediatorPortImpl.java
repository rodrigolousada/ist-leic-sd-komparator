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
	
	// lists for operations
	private Set<SupplierClient> clients = new HashSet<SupplierClient>();
	
	public Set<SupplierClient> getClients(){
		return clients;
	}
	
	private ListCartsResponse listcarts;
	// Main operations -------------------------------------------------------

	@Override
	public List<ItemView> getItems(String productId) throws InvalidItemId_Exception {
		List<ItemView> itemlist= new ArrayList<ItemView>();
		for(SupplierClient client : clients){
			try {
				ProductView product = client.getProduct(productId);
				
				ItemView item = new ItemView();
				ItemIdView aux_id = new ItemIdView();
				
				item.setDesc(product.getDesc());
				item.setPrice(product.getPrice());
				
				aux_id.setProductId(product.getId());
				aux_id.supplierId= client.getSupplierId();
				
				item.setItemId(aux_id);
				itemlist.add(item);
				
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
		// TODO Auto-generated method stub
		return null;
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
	
    // TODO

    
	// Exception helpers -----------------------------------------------------

    // TODO

}

package org.komparator.supplier.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Domain Root. */
public class Supplier {

	// Members ---------------------------------------------------------------

	/**
	 * Map of existing products. Uses concurrent hash table implementation
	 * supporting full concurrency of retrievals and high expected concurrency
	 * for updates.
	 */
	private Map<String, Product> products = new ConcurrentHashMap<>();

	/**
	 * Global purchase identifier counter. Uses lock-free thread-safe single
	 * variable.
	 */
	private AtomicInteger purchaseIdCounter = new AtomicInteger(0);

	/** Map of purchases. Also uses concurrent hash table implementation. */
	private Map<String, Purchase> purchases = new ConcurrentHashMap<>();

	// For more information regarding concurrent collections, see:
	// https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/package-summary.html#package.description

	// Singleton -------------------------------------------------------------

	/* Private constructor prevents instantiation from other classes */
	private Supplier() {
	}

	/**
	 * SingletonHolder is loaded on the first execution of
	 * Singleton.getInstance() or the first access to SingletonHolder.INSTANCE,
	 * not before.
	 */
	private static class SingletonHolder {
		private static final Supplier INSTANCE = new Supplier();
	}

	public static synchronized Supplier getInstance() {
		return SingletonHolder.INSTANCE;
	}

	// product ---------------------------------------------------------------

	public void reset() {
		products.clear();
		purchases.clear();
		purchaseIdCounter.set(0);
	}

	public Boolean productExists(String pid) {
		return products.containsKey(pid);
	}

	public Set<String> getProductsIDs() {
		return products.keySet();
	}

	public Product getProduct(String productId) {
		return products.get(productId);
	}

	public void registerProduct(String productId, String description, int quantity, int price) {
		if (acceptProduct(productId, description, quantity, price)) {
			products.put(productId, new Product(productId, description, quantity, price));
		}
	}

	private Boolean acceptProduct(String productId, String description, int quantity, int price) {
		return productId != null && !"".equals(productId) && description != null && !"".equals(description)
				&& quantity > 0 && price > 0;
	}

	public String buyProduct(String productId, int quantity) throws QuantityException {
		// update product
		Product product = getProduct(productId);
		decreaseProductQuantity(product, quantity);
		// create purchase record
		String purchaseId = generatePurchaseId(productId);
		Purchase purchase = new Purchase(purchaseId, productId, quantity, product.getPrice());
		purchases.put(purchaseId, purchase);
		return purchaseId;
	}

	private void decreaseProductQuantity(Product product, int quantity) throws QuantityException {
		// acquire lock to perform get and set together
		synchronized (product) {
			int currentQuantity = product.getQuantity();
			if (currentQuantity < quantity) {
				String message = String.format("Tried to buy %d units of product %s but only %d units are available.",
						quantity, product.getId(), currentQuantity);
				throw new QuantityException(message);
				// throw also releases lock
			}
			// set new quantity
			product.setQuantity(currentQuantity - quantity);
		}
		// release lock
	}

	// purchase --------------------------------------------------------------

	public Purchase getPurchase(String purchaseId) {
		return purchases.get(purchaseId);
	}

	private String generatePurchaseId(String pid) {
		// relying on AtomicInteger to make sure assigned number is unique
		int purchaseId = purchaseIdCounter.incrementAndGet();
		return Integer.toString(purchaseId);
	}

	public List<String> getPurchasesIDs() {
		List<Purchase> purchasesList = new ArrayList<>(purchases.values());
		// using comparator to sort result list
		Collections.sort(purchasesList, new Comparator<Purchase>() {
			public int compare(Purchase p1, Purchase p2) {
				return p1.getTimestamp().compareTo(p2.getTimestamp());
			}
		});
		List<String> idsList = new ArrayList<String>();
		for (Purchase p : purchasesList) {
			idsList.add(p.getPurchaseId());
		}
		return idsList;
	}

}
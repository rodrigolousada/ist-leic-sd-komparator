package org.komparator.supplier.domain;

import java.util.Date;

/**
 * Purchase entity. Immutable i.e. once an object is created it cannot be
 * changed.
 */
public class Purchase {
	/** Purchase identifier. */
	private String purchaseId;
	/** Purchased product identifier. */
	private String productId;
	/** Purchased quantity. */
	private int quantity;
	/** Unit price of purchased product. */
	private int unitPrice;
	/** Date of purchase. */
	private Date timestamp = new Date();

	/** Create a new purchase. */
	public Purchase(String pid, String productId, int quantity, int unitPrice) {
		this.purchaseId = pid;
		this.productId = productId;
		this.quantity = quantity;
		this.unitPrice = unitPrice;
	}

	public String getPurchaseId() {
		return purchaseId;
	}

	public String getProductId() {
		return productId;
	}

	public int getUnitPrice() {
		return unitPrice;
	}

	public int getQuantity() {
		return quantity;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Purchase [purchaseId=").append(purchaseId);
		builder.append(", productId=").append(productId);
		builder.append(", quantity=").append(quantity);
		builder.append(", unitPrice=").append(unitPrice);
		builder.append(", timestamp=").append(timestamp);
		builder.append("]");
		return builder.toString();
	}

}
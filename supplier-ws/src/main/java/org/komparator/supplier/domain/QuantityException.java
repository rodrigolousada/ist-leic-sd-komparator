package org.komparator.supplier.domain;

/** Exception used to signal a problem with the product quantity. */
public class QuantityException extends Exception {

	private static final long serialVersionUID = 1L;

	public QuantityException() {
	}

	public QuantityException(String message) {
		super(message);
	}

}

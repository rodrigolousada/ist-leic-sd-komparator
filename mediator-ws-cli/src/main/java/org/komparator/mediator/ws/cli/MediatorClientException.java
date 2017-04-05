package org.komparator.mediator.ws.cli;


public class MediatorClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public MediatorClientException() {
    }

    public MediatorClientException(String message) {
        super(message);
    }

    public MediatorClientException(Throwable cause) {
        super(cause);
    }

    public MediatorClientException(String message, Throwable cause) {
        super(message, cause);
    }

}

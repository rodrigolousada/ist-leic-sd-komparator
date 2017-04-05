package pt.ulisboa.tecnico.sdis.ws.cli;


public class CreditCardClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public CreditCardClientException() {
    }

    public CreditCardClientException(String message) {
        super(message);
    }

    public CreditCardClientException(Throwable cause) {
        super(cause);
    }

    public CreditCardClientException(String message, Throwable cause) {
        super(message, cause);
    }

}

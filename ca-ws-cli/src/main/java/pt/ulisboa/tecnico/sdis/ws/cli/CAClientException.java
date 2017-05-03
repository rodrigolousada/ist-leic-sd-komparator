package pt.ulisboa.tecnico.sdis.ws.cli;

public class CAClientException extends Exception {

	private static final long serialVersionUID = 1L;

	public CAClientException() {
	}

	public CAClientException(String message) {
		super(message);
	}

	public CAClientException(Throwable cause) {
		super(cause);
	}

	public CAClientException(String message, Throwable cause) {
		super(message, cause);
	}

}

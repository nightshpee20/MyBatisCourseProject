package exceptions;

public class IncompleteElementException extends Exception {
	public IncompleteElementException(String message) {
		super(message);
	}
	
	public IncompleteElementException(String message, Throwable cause) {
		super(message, cause);
	}
}

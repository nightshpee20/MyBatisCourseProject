package exceptions;

public class TooManyResultsException extends Exception {
	public TooManyResultsException(String message) {
		super(message);
	}
	
	public TooManyResultsException(String message, Throwable cause) {
		super(message, cause);
	}
}

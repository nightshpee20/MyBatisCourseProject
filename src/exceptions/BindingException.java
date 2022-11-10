package exceptions;

public class BindingException extends Exception {
	public BindingException(String message) {
		super(message);
	}
	
	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}
}

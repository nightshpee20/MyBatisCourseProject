package exceptions;

public class SqlSessionException extends Exception {
	public SqlSessionException(String message) {
		super(message);
	}
	
	public SqlSessionException(String message, Throwable cause) {
		super(message, cause);
	}
}

package unq.api.exceptions;

/**
 * Created by mrivero on 26/11/16.
 */
public class InvalidTokenException extends RuntimeException {

	public InvalidTokenException(String message) {
		super(message);
	}
}

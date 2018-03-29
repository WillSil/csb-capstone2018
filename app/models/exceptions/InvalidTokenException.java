package models.exceptions;

/**
 * Created by Corey Caplan on 10/25/17.
 */
public class InvalidTokenException extends Exception {

    public InvalidTokenException() {
    }

    public InvalidTokenException(String message) {
        super(message);
    }

    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

}

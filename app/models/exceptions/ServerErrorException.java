package models.exceptions;

/**
 * Created by Corey Caplan on 10/25/17.
 */
public class ServerErrorException extends Exception {

    public ServerErrorException() {
    }

    public ServerErrorException(String message) {
        super(message);
    }

    public ServerErrorException(Throwable cause) {
        super(cause);
    }

}

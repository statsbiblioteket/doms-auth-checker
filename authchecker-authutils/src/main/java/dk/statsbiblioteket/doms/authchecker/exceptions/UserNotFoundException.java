package dk.statsbiblioteket.doms.authchecker.exceptions;

/**
 * Created by IntelliJ IDEA.
 * User: abr
 * Date: Oct 1, 2010
 * Time: 2:47:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserNotFoundException extends BackendException {

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

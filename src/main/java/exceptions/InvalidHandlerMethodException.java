package exceptions;

public class InvalidHandlerMethodException extends Exception {
    public InvalidHandlerMethodException(String message) {
        super(message);
    }

    public InvalidHandlerMethodException(Exception e) {
        super(e);
    }
}

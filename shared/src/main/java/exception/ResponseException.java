package exception;

/**
 * Exception to be thrown when there is in error responding to an HTTP request
 */
public class ResponseException extends Exception {
    final private int statusCode;

    /**
     *
     * @param statusCode the HTTP status code for the error thrown
     * @param message message detailing the error thrown
     */
    public ResponseException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    /**
     *
     * @return integer containing the HTTP status code
     */
    public int statusCode() {
        return statusCode;
    }

    public String getMessage() {return super.getMessage();}

}

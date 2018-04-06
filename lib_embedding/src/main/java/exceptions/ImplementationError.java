package exceptions;

public class ImplementationError extends Error{
    /**
     * constructor
     * @param msg exception message
     */
    public ImplementationError(String msg) {
        super(msg);
    }
}

package exceptions;

public class TransformationException extends Exception{

    /**
     * constructor
     * @param msg exception message
     */
    public TransformationException(String msg) {
        super(msg);
    }
    public TransformationException(Exception e) {
        super(e);
    }
}

package gov.gtas.parsers.exception;

public class ParseRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ParseRuntimeException() {}
    public ParseRuntimeException(String message) {
        super(message);
    }
}

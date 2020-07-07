package gov.gtas.services;

public class NoPrimeFlightException extends LoaderException {

    public NoPrimeFlightException() {
    }

    public NoPrimeFlightException(String message) {
        super(message);
    }
}

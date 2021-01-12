package app.exceptions;

public class FlightOverflowException extends Throwable {
    public FlightOverflowException(String message) {
        super(message);
    }
}

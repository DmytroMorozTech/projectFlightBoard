package app.exceptions;

public class BookingOverflowException extends Throwable {
    public BookingOverflowException(String message) {
        super(message);
    }
}

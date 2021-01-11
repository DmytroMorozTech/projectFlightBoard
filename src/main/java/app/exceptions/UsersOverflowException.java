package app.exceptions;

public class UsersOverflowException extends Throwable {
    public UsersOverflowException(String message) {
        super(message);
    }
}
package pl.bartlomiej.securecapita.common.exception;

public class AccountVerificationException extends RuntimeException {
    public AccountVerificationException(String message) {
        super(message);
    }
}

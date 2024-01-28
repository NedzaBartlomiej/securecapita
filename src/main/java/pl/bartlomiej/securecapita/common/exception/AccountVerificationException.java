package pl.bartlomiej.securecapita.common.exception;

public class AccountVerificationException extends RuntimeException {
    public AccountVerificationException() {
        super("Provided code is invalid.");
    }
}

package pl.bartlomiej.securecapita.common.exception;

public class AccountVerificationException extends RuntimeException {
    public AccountVerificationException() {
        super("Verification failed. Verification identifier is invalid.");
    }
}

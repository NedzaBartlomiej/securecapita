package pl.bartlomiej.securecapita.common.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}

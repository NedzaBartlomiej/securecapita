package pl.bartlomiej.securecapita.common.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}

// todo: create more precise exceptions by messages in thrown ApiException
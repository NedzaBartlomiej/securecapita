package pl.bartlomiej.securecapita.common.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super("Resource not found.");
    }
}

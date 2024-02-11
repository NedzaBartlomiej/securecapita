package pl.bartlomiej.securecapita.common.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException() {
        super("Invalid or expired refresh token. Try to authenticate, it may solve the problem");
    }
}

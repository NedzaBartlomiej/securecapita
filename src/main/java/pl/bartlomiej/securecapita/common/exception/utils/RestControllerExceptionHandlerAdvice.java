package pl.bartlomiej.securecapita.common.exception.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.bartlomiej.securecapita.common.exception.*;
import pl.bartlomiej.securecapita.common.model.HttpResponse;
import pl.bartlomiej.securecapita.user.UserController;
import pl.bartlomiej.securecapita.user.dto.UserAuthDto;

import static java.util.Objects.requireNonNull;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class RestControllerExceptionHandlerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HttpResponse> handleMethodArgumentNotValidException(BindingResult bindingResult) {
        return ResponseEntity.status(BAD_REQUEST).body(
                ExceptionUtils.getErrorHttpResponse(
                        BAD_REQUEST, requireNonNull(
                                bindingResult.getFieldError()).getDefaultMessage())
        );
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> handleApiException(ApiException exception) {
        return ResponseEntity.status(exception.getHttpStatus()).body(
                ExceptionUtils.getErrorHttpResponse(exception.getHttpStatus(), exception.getMessage())
        );
    }

    @ExceptionHandler(AccountVerificationException.class)
    public ResponseEntity<HttpResponse> handleAccountVerificationException(AccountVerificationException exception) {
        return ResponseEntity.status(NOT_FOUND).body(
                ExceptionUtils.getErrorHttpResponse(NOT_FOUND, exception.getMessage())
        );
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> handleUserNotFoundException(UserNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(
                ExceptionUtils.getErrorHttpResponse(NOT_FOUND, exception.getMessage()).add(
                        linkTo(UserController.class)
                                .withRel("createUser")
                                .withType(POST.name()))
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HttpResponse> handleResourceNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity.status(NOT_FOUND).body(
                ExceptionUtils.getErrorHttpResponse(NOT_FOUND, exception.getMessage()));
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<HttpResponse> handleInvalidRefreshTokenException(InvalidRefreshTokenException exception) {
        return ResponseEntity.status(UNAUTHORIZED).body(
                ExceptionUtils.getErrorHttpResponse(UNAUTHORIZED, exception.getMessage()).add(
                        linkTo(methodOn(UserController.class)
                                .authenticateUser(new UserAuthDto(null, null)))
                                .withRel("authenticate")
                                .withType(POST.name()))
        );
    }

    @ExceptionHandler(JWTVerificationException.class)
    public void handleJWTVerificationException(JWTVerificationException exception, HttpServletResponse response) {
        ExceptionUtils.processException(exception, response);
    }
}

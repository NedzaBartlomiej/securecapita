package pl.bartlomiej.securecapita.common.exception.utils;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import pl.bartlomiej.securecapita.common.model.HttpResponse;

import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class ExceptionUtils {
    public static void processException(Exception exception, HttpServletResponse response) {
        switch (exception) {
            case AccessDeniedException ignoredAccessDeniedException -> writeResponse(response,
                    getErrorHttpResponse(FORBIDDEN, "You don't have the required permissions."));
            case BadCredentialsException ignoredBadCredentialsException -> writeResponse(response,
                    getErrorHttpResponse(UNAUTHORIZED, "Bad authentication credentials."));
            case AuthenticationException ignoredAuthenticationException -> writeResponse(response,
                    getErrorHttpResponse(UNAUTHORIZED, "You need to authenticate to access this resource."));
            case JWTVerificationException ignoredJwtVerificationException -> writeResponse(response,
                    getErrorHttpResponse(UNAUTHORIZED, "Invalid authorization token."));
            default -> {
                writeResponse(response,
                        getErrorHttpResponse(INTERNAL_SERVER_ERROR, "An error occured, try again."));
                log.error("Unhandled error message: {}, Exception {}", exception.getMessage(), exception.getClass().getName());
                log.error("Cause: {}, Stack Trace: {}", exception.getCause(), exception.getStackTrace());
            }
        }
    }

    public static HttpResponse getErrorHttpResponse(HttpStatus httpStatus, String message) {
        return HttpResponse.builder()
                .timestamp(now().toString())
                .statusCode(httpStatus.value())
                .httpStatus(httpStatus)
                .message(message)
                .build();
    }

    @SneakyThrows
    private static void writeResponse(HttpServletResponse response, HttpResponse httpResponse) {
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpResponse.getStatusCode());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        outputStream.close();
    }
}

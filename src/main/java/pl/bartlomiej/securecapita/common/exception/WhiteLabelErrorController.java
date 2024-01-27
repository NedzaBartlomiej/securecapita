package pl.bartlomiej.securecapita.common.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.securecapita.common.model.HttpResponse;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
public class WhiteLabelErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<HttpResponse> getEndpointNotFoundErrorResponse() {
        return ResponseEntity.status(NOT_FOUND).body(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(NOT_FOUND.value())
                        .httpStatus(NOT_FOUND)
                        .message("The requested endpoint was not found on the server.")
                        .build()
        );
    }
}

package pl.bartlomiej.securecapita.common.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_DEFAULT)
public class HttpResponse {
    protected String timestamp;
    protected int statusCode;
    protected HttpStatus httpStatus;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;
}

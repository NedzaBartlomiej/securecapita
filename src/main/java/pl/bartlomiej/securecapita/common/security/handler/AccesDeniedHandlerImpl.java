package pl.bartlomiej.securecapita.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import pl.bartlomiej.securecapita.common.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AccesDeniedHandlerImpl implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timestamp(now().toString())
                .statusCode(FORBIDDEN.value())
                .httpStatus(FORBIDDEN)
                .reason("You don't have the required permissions.")
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpResponse.getStatusCode());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
        outputStream.close();
    }
}

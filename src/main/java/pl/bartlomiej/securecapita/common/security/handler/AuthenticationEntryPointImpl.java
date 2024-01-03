package pl.bartlomiej.securecapita.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import pl.bartlomiej.securecapita.common.model.HttpResponse;

import java.io.IOException;
import java.io.OutputStream;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        HttpResponse httpResponse = HttpResponse.builder()
                .timestamp(now().toString())
                .statusCode(UNAUTHORIZED.value())
                .httpStatus(UNAUTHORIZED)
                .reason("You need to authenticate to access this resource.")
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

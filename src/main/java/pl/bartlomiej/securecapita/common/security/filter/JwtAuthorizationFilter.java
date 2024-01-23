package pl.bartlomiej.securecapita.common.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.common.security.auth.jwt.JwtTokenService;

import java.io.IOException;
import java.util.Map;

import static java.util.stream.Stream.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String EMAIL_REQUEST_KEY = "email";
    private static final String TOKEN_REQUEST_KEY = "token";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getRequestTokenClaims(request).get(TOKEN_REQUEST_KEY);
            String requestEmail = getRequestTokenClaims(request).get(EMAIL_REQUEST_KEY);
            if (jwtTokenService.isTokenValid(requestEmail, token)) {
                SecurityContextHolder.getContext()
                        .setAuthentication(
                                jwtTokenService.getAuthentication(
                                        requestEmail,
                                        jwtTokenService.getAuthoritiesFromRequestToken(token),
                                        request
                                ));
            } else {
                SecurityContextHolder.clearContext();
                log.info("Token validation failed.");
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            // todo: filterProcessError
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) == null ||
                !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(OPTIONS.name());
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
                .findAny().orElseThrow(() -> new ApiException("Missing Authorization Token."));
    }

    private Map<String, String> getRequestTokenClaims(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        return Map.of(
                EMAIL_REQUEST_KEY, jwtTokenService.getSubjectFromRequestToken(token, request),
                TOKEN_REQUEST_KEY, token
        );
    }
}

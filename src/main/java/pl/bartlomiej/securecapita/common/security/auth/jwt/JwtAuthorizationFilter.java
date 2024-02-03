package pl.bartlomiej.securecapita.common.security.auth.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.List;
import java.util.Map;

import static java.util.stream.Stream.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;
import static pl.bartlomiej.securecapita.common.exception.utils.ExceptionUtils.processException;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private static final String EMAIL_REQUEST_KEY = "email";
    private static final String TOKEN_REQUEST_KEY = "token";
    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String token = getRequestTokenClaims(request).get(TOKEN_REQUEST_KEY);
            String requestEmail = getRequestTokenClaims(request).get(EMAIL_REQUEST_KEY);
            if (jwtTokenService.isTokenValid(requestEmail, token)) {
                SecurityContextHolder.getContext()
                        .setAuthentication(
                                this.getAuthentication(
                                        requestEmail,
                                        jwtTokenService.getAuthoritiesFromRequestToken(token),
                                        request
                                ));
            }
            filterChain.doFilter(request, response);
        } catch (Exception exception) {
            SecurityContextHolder.clearContext();
            processException(exception, request, response);
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
                .findAny()
                .orElseThrow(() -> new JWTVerificationException("Missing authorization token."));
    }

    private Map<String, String> getRequestTokenClaims(HttpServletRequest request) throws JWTVerificationException {
        String token = getTokenFromRequest(request);
        return Map.of(
                EMAIL_REQUEST_KEY, jwtTokenService.getSubjectFromRequestToken(token),
                TOKEN_REQUEST_KEY, token
        );
    }

    private Authentication getAuthentication(String email, List<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        authenticationToken.setDetails(request);
        return authenticationToken;
    }
}

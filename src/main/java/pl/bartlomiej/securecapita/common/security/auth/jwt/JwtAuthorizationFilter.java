package pl.bartlomiej.securecapita.common.security.auth.jwt;

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

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.OPTIONS;
import static pl.bartlomiej.securecapita.common.exception.utils.ExceptionUtils.processException;
import static pl.bartlomiej.securecapita.common.security.auth.jwt.JwtTokenService.TOKEN_PREFIX;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenService jwtTokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        try {
            String token = jwtTokenService.getTokenFromRequest(request);
            String requestEmail = jwtTokenService.getSubjectFromRequestToken(token);
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
            processException(exception, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getHeader(AUTHORIZATION) == null ||
                !request.getHeader(AUTHORIZATION).startsWith(TOKEN_PREFIX) ||
                request.getMethod().equalsIgnoreCase(OPTIONS.name()) ||
                request.getRequestURI().contains("/auth/access-token");
    }

    private Authentication getAuthentication(String email, List<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        authenticationToken.setDetails(request);
        return authenticationToken;
    }
}

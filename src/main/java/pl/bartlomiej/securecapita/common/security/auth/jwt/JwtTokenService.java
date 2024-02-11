package pl.bartlomiej.securecapita.common.security.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.bartlomiej.securecapita.common.exception.InvalidRefreshTokenException;
import pl.bartlomiej.securecapita.user.User;
import pl.bartlomiej.securecapita.user.dto.UserDtoMapper;
import pl.bartlomiej.securecapita.user.dto.UserSecurityDto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static java.util.stream.Stream.ofNullable;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtTokenService {

    public static final String TOKEN_PREFIX = "Bearer ";
    private static final String TOKEN_ISSUER = "SECURECAPITA_APP";
    private static final String TOKEN_AUDIENCE = "SECURECAPITA";
    private static final String TOKEN_AUTHORITIES = "authorities";
    private static final Date ACCESS_TOKEN_EXPIRATION_DATE = new Date(currentTimeMillis() + 1_800_000L);
    private static final Date REFRESH_TOKEN_EXPIRATION_DATE = new Date(currentTimeMillis() + 432_000_000L);
    @Value(value = "${jwt.secret}")
    private String secret;

    public String createAccessToken(UserSecurityDto userSecurityDto) {
        return JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withAudience(TOKEN_AUDIENCE)
                .withIssuedAt(new Date())
                .withExpiresAt(ACCESS_TOKEN_EXPIRATION_DATE)
                .withSubject(userSecurityDto.getUsername())
                .withArrayClaim(TOKEN_AUTHORITIES, getAuthoritiesClaimFromUser(userSecurityDto))
                .sign(HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserSecurityDto userSecurityDto) {
        return JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withAudience(TOKEN_AUDIENCE)
                .withIssuedAt(new Date())
                .withExpiresAt(REFRESH_TOKEN_EXPIRATION_DATE)
                .withSubject(userSecurityDto.getUsername())
                .sign(HMAC512(secret.getBytes()));
    }

    public String refreshAccessToken(String refreshToken, User user) {
        if (!this.isTokenValid(this.getSubjectFromRequestToken(refreshToken), refreshToken)) {
            throw new InvalidRefreshTokenException();
        } else {
            return this.createAccessToken(UserDtoMapper.mapToSecurityDto(user));
        }
    }

    public boolean isTokenValid(String email, String token) {
        return isNotEmpty(email) && this.verify(token)
                .getExpiresAt().after(new Date());
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromRequestToken(String requestToken) {
        return Arrays.stream(this.verify(requestToken)
                        .getClaim(TOKEN_AUTHORITIES)
                        .asArray(String.class))
                .map(SimpleGrantedAuthority::new).toList();
    }

    public String getSubjectFromRequestToken(String token) {
        return this.verify(token).getSubject();
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        return ofNullable(request.getHeader(AUTHORIZATION))
                .filter(header -> header.startsWith(TOKEN_PREFIX))
                .map(token -> token.replace(TOKEN_PREFIX, EMPTY))
                .findAny()
                .orElseThrow(() -> new JWTVerificationException("Missing authorization token."));
    }

    private DecodedJWT verify(String token) throws JWTVerificationException {
        return require(HMAC512(secret))
                .withIssuer(TOKEN_ISSUER).build()
                .verify(token);
    }

    private String[] getAuthoritiesClaimFromUser(UserSecurityDto userSecurityDto) {
        return userSecurityDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }
}

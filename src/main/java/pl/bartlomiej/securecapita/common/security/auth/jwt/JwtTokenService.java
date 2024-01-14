package pl.bartlomiej.securecapita.common.security.auth.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.bartlomiej.securecapita.user.dto.UserSecurityDto;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.auth0.jwt.JWT.require;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static java.lang.System.currentTimeMillis;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Service
public class JwtTokenService {

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
                .withSubject(userSecurityDto.getId())
                .withArrayClaim(TOKEN_AUTHORITIES, getAuthoritiesClaimFromUser(userSecurityDto))
                .sign(HMAC512(secret.getBytes()));
    }

    public String createRefreshToken(UserSecurityDto userSecurityDto) {
        return JWT.create()
                .withIssuer(TOKEN_ISSUER)
                .withAudience(TOKEN_AUDIENCE)
                .withIssuedAt(new Date())
                .withExpiresAt(REFRESH_TOKEN_EXPIRATION_DATE)
                .withSubject(userSecurityDto.getId())
                .sign(HMAC512(secret.getBytes()));
    }

    public List<SimpleGrantedAuthority> getAuthoritiesFromRequestToken(String request_token) {
        return Arrays.stream(this.verify(request_token)
                        .getClaim(TOKEN_AUTHORITIES)
                        .asArray(String.class))
                .map(SimpleGrantedAuthority::new).toList();
    }

    public Authentication getAuthentication(String email, List<SimpleGrantedAuthority> authorities, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, null, authorities);
        authenticationToken.setDetails(request);
        return authenticationToken;
    }

    public String getSubject(String token, HttpServletRequest request) {
        try {
            return this.verify(token).getSubject();
        } catch (TokenExpiredException exception) {
            request.setAttribute("expiredMessage", exception.getMessage());
            throw exception;
        } catch (InvalidClaimException exception) {
            request.setAttribute("invalidClaimMessage", exception.getMessage());
            throw exception;
        }
    }

    public boolean isTokenValid(String email, String token) {
        return isNotEmpty(email) && this.verify(token)
                .getExpiresAt().before(new Date());
    }

    private DecodedJWT verify(String token) throws TokenExpiredException, InvalidClaimException {
        try {
            return require(HMAC512(secret))
                    .withIssuer(TOKEN_ISSUER).build()
                    .verify(token);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("JWT Token verificatinon failed.");
        }
    }

    private String[] getAuthoritiesClaimFromUser(UserSecurityDto userSecurityDto) {
        return userSecurityDto.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toArray(String[]::new);
    }
}

package pl.bartlomiej.securecapita.common.security.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    public Authentication authenticate(String email, String password) {
        return authenticationManager.authenticate(unauthenticated(email, password));
    }
}

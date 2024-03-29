package pl.bartlomiej.securecapita.common.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiej.securecapita.user.UserService;
import pl.bartlomiej.securecapita.user.dto.UserSecurityDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userService.getUserByEmail(email)
                .map(UserSecurityDto::new)
                .orElseThrow(() -> {
                    log.error("User not found in the database. class: {}", UserDetailsServiceImpl.class.getName());
                    return new UsernameNotFoundException("User not found in the database. class: " + getClass().getName());
                });
    }
}

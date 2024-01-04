package pl.bartlomiej.securecapita.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.bartlomiej.securecapita.user.UserRepository;
import pl.bartlomiej.securecapita.user.dto.UserSecurityDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.getUserByEmail(username).map(user -> {
            log.info("User found in the database. class: {}, user: {}", UserDetailsServiceImpl.class.getName(), user);
            return new UserSecurityDto(user);
        }).orElseThrow(() -> {
            log.error("User not found in the database. class: {}", UserDetailsServiceImpl.class.getName());
            return new UsernameNotFoundException("User not found in the database. class: " + UserDetailsServiceImpl.class.getName());
        });
    }
}

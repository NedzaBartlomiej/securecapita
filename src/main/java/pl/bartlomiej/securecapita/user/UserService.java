package pl.bartlomiej.securecapita.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.role.RoleRepository;
import pl.bartlomiej.securecapita.user.dto.UserCreateDto;
import pl.bartlomiej.securecapita.user.dto.UserDtoMapper;
import pl.bartlomiej.securecapita.user.dto.UserReadDto;
import pl.bartlomiej.securecapita.verification.Verification;
import pl.bartlomiej.securecapita.verification.VerificationRepository;

import java.util.UUID;

import static pl.bartlomiej.securecapita.role.Role.RoleType.ROLE_USER;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.EMAIL_VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationRepository verificationRepository;
    private final RoleRepository roleRepository;

    public UserReadDto register(UserCreateDto user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new ApiException("E-mail already in use, please use another e-mail.");
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(roleRepository.getRoleByName(ROLE_USER.name()));
            User savedUser = userRepository.save(UserDtoMapper.map(user));
            String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), EMAIL_VERIFICATION.name().toLowerCase());
            verificationRepository.save(Verification.builder()
                    .user(savedUser)
                    .verificationType(EMAIL_VERIFICATION.name())
                    .verificationIdentifier(verificationUrl).build());
//            emailService.sendVerificationEmail(savedUser.getFirstName(), savedUser.getEmail(), verificationUrl, EMAIL_VERIFICATION.name());
            return UserDtoMapper.map(savedUser);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occured.");
        }
    }

    private String getVerificationUrl(String key, String verificationType) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                        "user/" + verificationType.toLowerCase() + "/" + key)
                .toUriString();
    }
}

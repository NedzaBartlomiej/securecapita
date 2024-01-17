package pl.bartlomiej.securecapita.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.role.RoleRepository;
import pl.bartlomiej.securecapita.user.dto.UserCreateDto;
import pl.bartlomiej.securecapita.user.dto.UserDtoMapper;
import pl.bartlomiej.securecapita.user.dto.UserReadDto;
import pl.bartlomiej.securecapita.verification.VerificationService;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static pl.bartlomiej.securecapita.role.RoleType.ROLE_USER;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.EMAIL_VERIFICATION;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final VerificationService verificationService;
    private final RoleRepository roleRepository;

    public UserReadDto create(UserCreateDto user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new ApiException("E-mail already in use, please use another e-mail.");
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setRole(roleRepository.getRoleByName(ROLE_USER.name()));
            User savedUser = userRepository.save(UserDtoMapper.mapFromCreateDto(user));
            verificationService.handleVerification(savedUser, EMAIL_VERIFICATION);
            return UserDtoMapper.mapToReadDto(savedUser);
        } catch (EmptyResultDataAccessException exception) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new ApiException("An error occured.");
        }
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public User verifyMfaUser(String email, String code) {
        LocalDateTime codeExpirationDate = verificationService
                .getExpirationDateByVerificationIdentifier(code);

        User loggingInUser = userRepository.getUserByEmail(email)
                .orElseThrow(() -> new ApiException("Logging in user not found."));

        return verificationService.getUserByVerificationIdentifier(code)
                .filter(user -> user.equals(loggingInUser))
                .filter(user -> codeExpirationDate.isAfter(now()))
                .map(user -> {
                    verificationService.deleteVerificationByVerificationIdentifier(code);
                    return user;
                })
                .orElseThrow(() -> {
                    if (codeExpirationDate != null && !codeExpirationDate.isAfter(now())) {
                        verificationService.deleteVerificationByVerificationIdentifier(code);
                        return new ApiException("Provided code has expired.");
                    } else {
                        return new ApiException("Provided code is invalid.");
                    }
                });
    }
}
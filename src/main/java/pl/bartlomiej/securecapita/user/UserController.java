package pl.bartlomiej.securecapita.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.common.model.HttpResponse;
import pl.bartlomiej.securecapita.common.security.auth.jwt.JwtTokenService;
import pl.bartlomiej.securecapita.user.dto.UserAuthDto;
import pl.bartlomiej.securecapita.user.dto.UserCreateDto;
import pl.bartlomiej.securecapita.user.dto.UserDtoMapper;
import pl.bartlomiej.securecapita.user.dto.UserReadDto;
import pl.bartlomiej.securecapita.verification.Verification;
import pl.bartlomiej.securecapita.verification.VerificationService;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.security.authentication.UsernamePasswordAuthenticationToken.unauthenticated;

@RequiredArgsConstructor
@RestController
@RequestMapping("/securecapita-api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;
    private final JwtTokenService jwtTokenService;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody @Valid UserCreateDto user) {
        UserReadDto userReadDto = userService.create(user);
        userReadDto.add(
                linkTo(UserController.class).slash(userReadDto.getId()).withSelfRel());
        return ResponseEntity.status(CREATED).body(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(CREATED.value())
                        .httpStatus(CREATED)
                        .message("User registered.")
                        .data(of("user", userReadDto))
                        .build()
        );
    }

    @PostMapping("/auth")
    public ResponseEntity<HttpResponse> authenticateUser(@RequestBody @Valid UserAuthDto userAuthDto) {
        authenticationManager.authenticate(unauthenticated(userAuthDto.email(), userAuthDto.password()));
        return userService.getUserByEmail(userAuthDto.email())
                .map(user ->
                        user.getUsingMfa()
                                ? smsVerificationCodeResponseOperation(user)
                                : sendAuthResponse(user))
                .orElseThrow(() -> new ApiException("User not found."));
    }

    @PostMapping("{id}/auth/verifications/mfa_verification/{code}")
    public ResponseEntity<HttpResponse> authenticateMfaUser(
            @PathVariable("id") Long id, @PathVariable("code") String code) {
        User user = userService.verifyMfaUser(id, code);
        return sendAuthResponse(user);
    }

    //todo: {id}/auth/verifications/email_verification/{key} POST

    @GetMapping("profile-test")
    public ResponseEntity<HttpResponse> getAuthenticatedUser(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName())
                .map(user ->
                        ResponseEntity.ok(
                                HttpResponse.builder()
                                        .timestamp(now().toString())
                                        .statusCode(OK.value())
                                        .httpStatus(OK)
                                        .data(of("user", UserDtoMapper.mapToReadDto(user)))
                                        .build()))
                .orElseThrow(() -> new ApiException("User not found."));
    }

    private ResponseEntity<HttpResponse> smsVerificationCodeResponseOperation(User user) {
        verificationService.handleVerification(user, Verification.VerificationType.MFA_VERIFICATION);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .message("Verification code sent.")
                        .data(of("user", UserDtoMapper.mapToReadDto(user)))
                        .build());
    }

    private ResponseEntity<HttpResponse> sendAuthResponse(User user) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .message("User authenticated.")
                        .data(of(
                                "user", UserDtoMapper.mapToReadDto(user),
                                "accessToken", jwtTokenService.createAccessToken(UserDtoMapper.mapToSecurityDto(user)),
                                "refreshToken", jwtTokenService.createRefreshToken(UserDtoMapper.mapToSecurityDto(user))
                        ))
                        .build());
    }
}

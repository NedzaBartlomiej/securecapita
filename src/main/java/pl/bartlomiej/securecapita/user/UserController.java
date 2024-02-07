package pl.bartlomiej.securecapita.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.common.exception.UserNotFoundException;
import pl.bartlomiej.securecapita.common.model.HttpResponse;
import pl.bartlomiej.securecapita.common.security.auth.AuthenticationService;
import pl.bartlomiej.securecapita.common.security.auth.jwt.JwtTokenService;
import pl.bartlomiej.securecapita.user.dto.*;
import pl.bartlomiej.securecapita.verification.Verification;
import pl.bartlomiej.securecapita.verification.VerificationService;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.http.HttpMethod.*;
import static org.springframework.http.HttpStatus.*;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.RESET_PASSWORD_VERIFICATION;

@RequiredArgsConstructor
@RestController
@RequestMapping("/securecapita-api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationService authenticationService;
    private final VerificationService verificationService;
    private final JwtTokenService jwtTokenService;

    @PostMapping
    public ResponseEntity<HttpResponse> createUser(@RequestBody @Valid UserCreateDto user) {
        UserReadDto userReadDto = userService.create(user);
        return ResponseEntity.status(CREATED).body(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(CREATED.value())
                        .httpStatus(CREATED)
                        .message("User registered.")
                        .data(of("user", this.addUserSelfRelLink(userReadDto)))
                        .build()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<HttpResponse> getAuthenticatedUser(@PathVariable Long id, Authentication authentication) {
        User authenticatedUser = userService.getUserByEmail(authentication.getName())
                .orElseThrow(UserNotFoundException::new);

        return userService.getUserById(id)
                .map(user -> {
                    if (!user.equals(authenticatedUser)) {
                        throw new ApiException("You try to access not your account resorces.", FORBIDDEN);
                    } else {
                        return ResponseEntity.ok(
                                this.getUserResponse(user));
                    }
                })
                .orElseThrow(UserNotFoundException::new);
    }

    // AUTHENTICATION FEATURE

    @PostMapping("/auth")
    public ResponseEntity<HttpResponse> authenticateUser(@RequestBody @Valid UserAuthDto userAuthDto) {
        Authentication authentication = authenticationService.authenticate(userAuthDto.email(), userAuthDto.password());
        UserSecurityDto authenticatedUser = (UserSecurityDto) authentication.getPrincipal();
        return userService.getUserByEmail(authenticatedUser.getUsername())
                .map(user ->
                        user.getUsingMfa()
                                ? smsVerificationCodeResponseOperation(user)
                                : getAuthResponse(user))
                .orElseThrow(UserNotFoundException::new);
    }

    @DeleteMapping("/{id}/auth/verifications/mfa-verification/{code}")
    public ResponseEntity<HttpResponse> authenticateMfaUser(
            @PathVariable("id") Long id, @PathVariable("code") String code) {
        return getAuthResponse(
                userService.verifyMfaUser(id, code));
    }

    //todo: {id}/auth/verifications/email_verification/{key} POST

    // RESET PASSWORD FEATURE

    @PostMapping("/verifications/reset-password-verification")
    public ResponseEntity<HttpResponse> sendResetPasswordVerificationEmail(
            @RequestBody @Valid UserEmailRequest userEmailRequest) {
        return userService.getUserByEmail(userEmailRequest.getEmail())
                .map(user -> {
                    verificationService.handleVerification(user, RESET_PASSWORD_VERIFICATION);
                    return ResponseEntity.ok(
                            getOkResponseWithMessage("Verification email sent."));
                })
                .orElseThrow(UserNotFoundException::new);
    }

    @PatchMapping("/verifications/reset-password-verification/{identifier}")
    public ResponseEntity<HttpResponse> verifyResetPasswordLink(@PathVariable String identifier) {
        User linkOwner = verificationService.verifyResetPasswordIdentifier(identifier);
        return ResponseEntity.ok(
                this.getUserResponse(linkOwner)
                        .add(linkTo(
                                methodOn(UserController.class)
                                        .resetUserPassword(
                                                linkOwner.getId(),
                                                identifier,
                                                ResetUserPasswordRequest.builder().build()))
                                .withRel("resetPassword")
                                .withType(PATCH.name()))
        );
    }

    @PatchMapping("/{id}/verifications/reset-password-verification/{identifier}")
    public ResponseEntity<HttpResponse> resetUserPassword(
            @PathVariable Long id, @PathVariable String identifier,
            @RequestBody @Valid ResetUserPasswordRequest passwordRequest) {
        userService.resetUserPassword
                (id, identifier, passwordRequest.getPassword(), passwordRequest.getPasswordConfirmation());
        return ResponseEntity.ok(
                this.getOkResponseWithMessage("Password has been changed successfully."));
    }

    // RESPONSE OPERATIONS

    private ResponseEntity<HttpResponse> smsVerificationCodeResponseOperation(User user) {
        verificationService.handleVerification(user, Verification.VerificationType.MFA_VERIFICATION);
        return ResponseEntity.ok(
                getOkResponseWithMessage("Verification code sent.")
                        .add(linkTo(
                                methodOn(UserController.class)
                                        .authenticateMfaUser(user.getId(), "sms-code"))
                                .withRel("authenticateMfaUser")
                                .withType(DELETE.name()))
        );
    }

    private ResponseEntity<HttpResponse> getAuthResponse(User user) {
        UserReadDto userReadDto = UserDtoMapper.mapToReadDto(user);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .message("User authenticated.")
                        .data(of(
                                "user", this.addUserSelfRelLink(userReadDto),
                                "accessToken", jwtTokenService.createAccessToken(UserDtoMapper.mapToSecurityDto(user)),
                                "refreshToken", jwtTokenService.createRefreshToken(UserDtoMapper.mapToSecurityDto(user))
                        ))
                        .build());
    }

    private HttpResponse getOkResponseWithMessage(String message) {
        return HttpResponse.builder()
                .timestamp(now().toString())
                .statusCode(OK.value())
                .httpStatus(OK)
                .message(message)
                .build();
    }

    private HttpResponse getUserResponse(User user) {
        return HttpResponse.builder()
                .timestamp(now().toString())
                .statusCode(OK.value())
                .httpStatus(OK)
                .data(of("user", UserDtoMapper.mapToReadDto(user)))
                .build();
    }

    private UserReadDto addUserSelfRelLink(UserReadDto userReadDto) {
        return userReadDto.add(
                linkTo(UserController.class).slash(userReadDto.getId())
                        .withSelfRel()
                        .withType(GET.name()));
    }
}

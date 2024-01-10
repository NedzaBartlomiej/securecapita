package pl.bartlomiej.securecapita.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.securecapita.common.exception.ApiException;
import pl.bartlomiej.securecapita.common.model.HttpResponse;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/securecapita-api/v1/users")
public class UserController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final VerificationService verificationService;

    @PostMapping("/auth")
    public ResponseEntity<HttpResponse> authenticateUser(@RequestBody @Valid UserAuthDto authRequest) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.email(), authRequest.password()));
        return userService.getUserByEmail(authRequest.email())
                .map(user ->
                        user.getUsingMfa()
                                ? sendSmsVerificationCode(user)
                                : sendAuthResponse(UserDtoMapper.map(user)))
                .orElseThrow(() -> new ApiException("User not found."));
    }


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

    private ResponseEntity<HttpResponse> sendSmsVerificationCode(User user) {
        verificationService.handleVerification(user, Verification.VerificationType.MFA_VERIFICATION);
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .message("Verification code sent.")
                        .data(of("user", UserDtoMapper.map(user)))
                        .build());
    }

    private ResponseEntity<HttpResponse> sendAuthResponse(UserReadDto userReadDto) {
        return ResponseEntity.ok(
                HttpResponse.builder()
                        .timestamp(now().toString())
                        .statusCode(OK.value())
                        .httpStatus(OK)
                        .message("User authenticated.")
                        .data(of("user", userReadDto))
                        .build());
    }
}

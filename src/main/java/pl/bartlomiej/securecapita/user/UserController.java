package pl.bartlomiej.securecapita.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.bartlomiej.securecapita.common.model.HttpResponse;
import pl.bartlomiej.securecapita.user.dto.UserCreateDto;
import pl.bartlomiej.securecapita.user.dto.UserReadDto;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/securecapita-api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<HttpResponse> registerUser(@RequestBody @Valid UserCreateDto user) {
        UserReadDto userReadDto = userService.register(user);
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
}

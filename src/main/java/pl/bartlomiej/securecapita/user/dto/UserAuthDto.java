package pl.bartlomiej.securecapita.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserAuthDto(@NotBlank(message = "Email cannot be empty.") String email,
                          @NotBlank(message = "Password field cannot be empty.") String password) {
}

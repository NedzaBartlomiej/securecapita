package pl.bartlomiej.securecapita.user.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthRequest(@NotBlank(message = "Email cannot be empty.") String email,
                          @NotBlank(message = "Password field cannor be empty.") String password) {
}

package pl.bartlomiej.securecapita.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetUserPasswordRequest {

    @NotBlank(message = "Password cannot be empty.")
    private String password;

    @NotBlank(message = "Password cannot be empty.")
    private String passwordConfirmation;
}

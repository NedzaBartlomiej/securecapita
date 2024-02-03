package pl.bartlomiej.securecapita.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEmailRequest {

    @NotBlank(message = "E-mail cannot be empty.")
    @Email(message = "E-mail should have correct form.")
    private String email;
}

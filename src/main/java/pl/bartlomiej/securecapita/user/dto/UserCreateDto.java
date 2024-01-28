package pl.bartlomiej.securecapita.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.bartlomiej.securecapita.user.role.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCreateDto {
    private Role role;

    @NotBlank(message = "First name cannot be empty.")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty.")
    private String lastName;

    @NotBlank(message = "E-mail cannot be empty.")
    @Email(message = "E-mail should have correct form.")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    private String password;
}

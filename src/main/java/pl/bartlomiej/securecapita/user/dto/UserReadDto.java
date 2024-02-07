package pl.bartlomiej.securecapita.user.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import pl.bartlomiej.securecapita.user.nestedentity.phone.Phone;
import pl.bartlomiej.securecapita.user.nestedentity.role.Role;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserReadDto extends RepresentationModel<UserReadDto> {

    private Role role;

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String address;

    private Phone phone;

    private String jobTitle;

    private String bio;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Boolean isEnabled;

    private Boolean isNotLocked;

    private Boolean usingMfa;
}

package pl.bartlomiej.securecapita.user.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import pl.bartlomiej.securecapita.event.UserEvent;
import pl.bartlomiej.securecapita.role.Role;

import java.time.LocalDateTime;
import java.util.List;

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

    private String phoneNumber;

    private String jobTitle;

    private String bio;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Boolean isEnabled;

    private Boolean isNotLocked;

    private Boolean isUsingMfa;
}

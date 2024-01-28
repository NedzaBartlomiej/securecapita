package pl.bartlomiej.securecapita.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import pl.bartlomiej.securecapita.user.nestedentity.phone.Phone;
import pl.bartlomiej.securecapita.user.nestedentity.role.Role;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_DEFAULT;
import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(NON_DEFAULT)
@DynamicInsert
@Entity
//todo: create cache for user selects
public class User {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String address;

    @OneToOne
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    private Phone phone;

    private String jobTitle;

    private String bio;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Boolean isEnabled;

    private Boolean isNotLocked;

    private Boolean usingMfa;
}

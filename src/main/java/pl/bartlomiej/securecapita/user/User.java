package pl.bartlomiej.securecapita.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import pl.bartlomiej.securecapita.event.UserEvent;
import pl.bartlomiej.securecapita.role.Role;

import java.time.LocalDateTime;
import java.util.List;

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
public class User {
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    List<UserEvent> userEvents;

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

    private String phoneNumber;

    //todo: add the phoneNumber countryCode (for SmsService functionallity)

    private String jobTitle;

    private String bio;

    private String imageUrl;

    private LocalDateTime createdAt;

    private Boolean isEnabled;

    private Boolean isNotLocked;

    private Boolean usingMfa;
}

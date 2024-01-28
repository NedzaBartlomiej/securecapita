package pl.bartlomiej.securecapita.user.nestedentity.event;

import jakarta.persistence.*;
import lombok.*;
import pl.bartlomiej.securecapita.user.User;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserEvent {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    private String device;

    private String ipAddress;

    private LocalDateTime eventTime;
}

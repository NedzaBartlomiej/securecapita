package pl.bartlomiej.securecapita.verification;

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
public class Verification {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(referencedColumnName = "id")
    private User user;

    private String verificationType;

    private String verificationIdentifier;

    private LocalDateTime expirationDate;

    public enum VerificationType {
        EMAIL_VERIFICATION, RESET_PASSWORD_VERIFICATION, MFA_VERIFICATION
    }
}

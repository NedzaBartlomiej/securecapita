package pl.bartlomiej.securecapita.verification;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import pl.bartlomiej.securecapita.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    @Transactional
    @Modifying
    void deleteVerificationByUserAndVerificationType(User user, String verificationType);

    @Transactional
    @Modifying
    void deleteVerificationByVerificationIdentifier(String verificationIdentifier);

    @Query("SELECT u FROM Verification v JOIN v.user u WHERE v.verificationIdentifier = :verificationIdentifier")
    Optional<User> getUserByVerificationIdentifier(String verificationIdentifier);

    @Query("SELECT v.expirationDate FROM Verification v WHERE v.verificationIdentifier=:identifier")
    LocalDateTime getExpirationDateByVerificationIdentifier(String identifier);
}

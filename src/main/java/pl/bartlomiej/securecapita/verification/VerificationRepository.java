package pl.bartlomiej.securecapita.verification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Query("SELECT v.isVerified FROM Verification v WHERE v.verificationIdentifier = :identifier")
    boolean isVerifiedByVerificationIdentifier(String identifier);

    @Transactional
    @Modifying
    @Query("UPDATE Verification v SET v.isVerified = :value WHERE v.verificationIdentifier = :identifier")
    void updateIsVerifiedByIdentifier(String identifier, boolean value);

    @Query("SELECT u FROM Verification v JOIN v.user u WHERE v.verificationIdentifier = :identifier")
    Optional<User> getUserByVerificationIdentifier(String identifier);

    @Query("SELECT v.expirationDate FROM Verification v WHERE v.verificationIdentifier=:identifier")
    LocalDateTime getExpirationDateByVerificationIdentifier(String identifier);
}

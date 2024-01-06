package pl.bartlomiej.securecapita.verification;

import jakarta.transaction.Transactional;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import pl.bartlomiej.securecapita.user.User;

public interface VerificationRepository extends JpaRepository<Verification, Long> {

    @Transactional
    @Modifying
    void deleteVerificationByUserAndVerificationType(User user, String verificationType);
}

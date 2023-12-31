package pl.bartlomiej.securecapita.verification;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationRepository extends JpaRepository<Verification, Long> {
}

package pl.bartlomiej.securecapita.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bartlomiej.securecapita.smsapi.SmsService;
import pl.bartlomiej.securecapita.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.EMAIL_VERIFICATION;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.MFA_VERIFICATION;

@Service
@Slf4j
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final SmsService smsService;

    public VerificationService(VerificationRepository verificationRepository, @Qualifier("TwilioService") SmsService smsService) {
        this.verificationRepository = verificationRepository;
        this.smsService = smsService;
    }

    public void handleVerification(User user, Verification.VerificationType verificationType) {
        switch (verificationType) {
            case MFA_VERIFICATION -> {
                String verificationCode = randomAlphabetic(8).toLowerCase();
                verificationRepository.deleteVerificationByUserAndVerificationType(user, MFA_VERIFICATION.name());
                verificationRepository.save(
                        Verification.builder()
                                .user(user)
                                .verificationType(verificationType.name())
                                .verificationIdentifier(verificationCode)
                                .expirationDate(LocalDateTime.now().plusHours(24))
                                .build());
                System.out.println("sendSms is disabled, cause: payment. Uncomment to use.");
                log.info("From: SecureCapita | Verification code: {}", verificationCode);
//                smsService.sendSms(user.getPhoneNumber(), "From: SecureCapita \nVerification code: \n" + verificationCode);
            }
            case EMAIL_VERIFICATION -> {
                String identifier = randomUUID().toString();
                verificationRepository.save(Verification.builder()
                        .user(user)
                        .verificationType(EMAIL_VERIFICATION.name())
                        .verificationIdentifier(identifier).build());
                //todo emailService.sendEmail(
                // savedUser.getFirstName(),
                // savedUser.getEmail(),
                // this.buildVerificationUrl(identifier, EMAIL_VERIFICATION.name()),
                // EMAIL_VERIFICATION.name());
            }
            //todo: other cases
        }
    }

    public Optional<User> getUserByVerificationIdentifier(String identifier) {
        return verificationRepository.getUserByVerificationIdentifier(identifier);
    }

    public void deleteVerificationByVerificationIdentifier(String verificationIdentifier) {
        verificationRepository.deleteVerificationByVerificationIdentifier(verificationIdentifier);
    }

    public LocalDateTime getExpirationDateByVerificationIdentifier(String identifier) {
        return verificationRepository.getExpirationDateByVerificationIdentifier(identifier);
    }

    private String buildVerificationUrl(String identifier, String verificationType) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/auth/verifications/" + verificationType.toLowerCase() + "/" + identifier)
                .build()
                .toUriString();
    }
}
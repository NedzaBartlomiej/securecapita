package pl.bartlomiej.securecapita.verification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bartlomiej.securecapita.common.exception.AccountVerificationException;
import pl.bartlomiej.securecapita.sms.SmsService;
import pl.bartlomiej.securecapita.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.*;

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
//                smsService.sendSms(user.getPhone().getCountryPrefix(), user.getPhone().getPhoneNumber(), "From: SecureCapita \nVerification code: \n" + verificationCode);
            }
            case EMAIL_VERIFICATION -> {
                String identifier = randomUUID().toString();
                verificationRepository.save(Verification.builder()
                        .user(user)
                        .verificationType(EMAIL_VERIFICATION.name())
                        .verificationIdentifier(identifier).build());
                log.info(this.buildVerificationUrl(identifier, EMAIL_VERIFICATION.name()));
                //todo emailService.sendEmail(
                // user.getFirstName(),
                // user.getEmail(),
                // this.buildVerificationUrl(identifier, EMAIL_VERIFICATION.name()),
                // EMAIL_VERIFICATION.name());
            }
            case RESET_PASSWORD_VERIFICATION -> {
                String identifier = randomUUID().toString();
                verificationRepository.deleteVerificationByUserAndVerificationType(
                        user, RESET_PASSWORD_VERIFICATION.name());
                verificationRepository.save(Verification.builder()
                        .user(user)
                        .verificationType(RESET_PASSWORD_VERIFICATION.name())
                        .verificationIdentifier(identifier)
                        .expirationDate(LocalDateTime.now().plusHours(24))
                        .build());
                log.info(this.buildVerificationUrl(identifier, RESET_PASSWORD_VERIFICATION.name()));
                //todo emailService.sendEmail(
                // user.getFirstName(),
                // user.getEmail(),
                // this.buildVerificationUrl(identifier, RESET_PASSWORD_VERIFICATION.name()),
                // EMAIL_VERIFICATION.name());
            }
        }
    }

    public User verifyResetPasswordIdentifier(String identifier) {
        LocalDateTime linkExpirationDate =
                verificationRepository.getExpirationDateByVerificationIdentifier(identifier);

        return verificationRepository.getUserByVerificationIdentifier(identifier)
                .filter(user -> linkExpirationDate.isAfter(now()))
                .map(user -> {
                    verificationRepository.updateIsVerifiedByIdentifier(identifier, true);
                    return user;
                })
                .orElseThrow(AccountVerificationException::new);
    }

    public User verifyEmailVerificationIdentifier(String identifier) {
        return verificationRepository.getUserByVerificationIdentifier(identifier)
                .orElseThrow(AccountVerificationException::new);
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

    public boolean isVerified(String identifier) {
        return verificationRepository.isVerifiedByVerificationIdentifier(identifier);
    }

    private String buildVerificationUrl(String identifier, String verificationType) {
        final String USERS_ROOT_RESOURCE_PATH = "/securecapita-api/v1/users";
        final String VERIFICATION_ROOT_PATH =
                "/verifications/" +
                        verificationType.replace("_", "-").toLowerCase() +
                        "/" + identifier;
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USERS_ROOT_RESOURCE_PATH + VERIFICATION_ROOT_PATH)
                .build()
                .toUriString();
    }
}

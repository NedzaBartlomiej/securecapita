package pl.bartlomiej.securecapita.verification;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.bartlomiej.securecapita.smsapi.SmsService;
import pl.bartlomiej.securecapita.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.EMAIL_VERIFICATION;
import static pl.bartlomiej.securecapita.verification.Verification.VerificationType.MFA_VERIFICATION;

@Service
public class VerificationService {
    private final VerificationRepository verificationRepository;
    private final SmsService smsService;

    public VerificationService(VerificationRepository verificationRepository, @Qualifier("TwilioService") SmsService smsService) {
        this.verificationRepository = verificationRepository;
        this.smsService = smsService;
    }

    public void sendVerification(User user, Verification.VerificationType verificationType) {
        switch (verificationType) {
            case MFA_VERIFICATION -> {
                String code = randomAlphabetic(8).toLowerCase();
                verificationRepository.deleteVerificationByUserAndVerificationType(user, MFA_VERIFICATION.name());
                verificationRepository.save(
                        Verification.builder()
                                .user(user)
                                .verificationType(verificationType.name())
                                .verificationIdentifier(code)
                                .expirationDate(LocalDateTime.now().plusHours(24))
                                .build());
                System.out.println("sendSms is disabled, cause: payment. Uncomment to use.");
//                smsService.sendSms(user.getPhoneNumber(), "From: SecureCapita \nVerification code: \n" + code);
            }
            case EMAIL_VERIFICATION -> {
                String verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), EMAIL_VERIFICATION.name().toLowerCase());
                verificationRepository.save(Verification.builder()
                        .user(user)
                        .verificationType(EMAIL_VERIFICATION.name())
                        .verificationIdentifier(verificationUrl).build());
                //todo emailService.sendVerificationEmail(savedUser.getFirstName(), savedUser.getEmail(), verificationUrl, EMAIL_VERIFICATION.name());
            }
            //todo: other cases
        }
    }

    private String getVerificationUrl(String key, String verificationType) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path(
                        "users/" + verificationType.toLowerCase() + "/" + key)
                .toUriString();
    }
}

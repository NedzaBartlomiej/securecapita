package pl.bartlomiej.securecapita.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.twilio.rest.api.v2010.account.Message.creator;


@Service("TwilioService")
@RequiredArgsConstructor
@Slf4j
public class TwilioService implements SmsService {
    private static final String FROM_NUMBER = "+12019570509";
    private static final String SID_KEY = "AC8365fc3646ef2f831434952a4a7d0f77"; //todo: create env var in docker
    private static final String TOKEN_KEY = "ac3cf1a0a808b07617f053bca40ad1be"; //todo: create env var in docker

    @Override
    public void sendSms(String country_prefix, String phoneNumber, String message) {
        Twilio.init(SID_KEY, TOKEN_KEY);
        Message smsMessage = creator(
                new PhoneNumber(country_prefix + phoneNumber),
                new PhoneNumber(FROM_NUMBER),
                message
        ).create();
        log.info("Sms verification message sent. Sms Api Provider class: {}, Message: {}", getClass().getName(), smsMessage.toString());
    }
}

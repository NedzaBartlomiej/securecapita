package pl.bartlomiej.securecapita.smsapi;

import pl.bartlomiej.securecapita.user.User;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}

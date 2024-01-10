package pl.bartlomiej.securecapita.smsapi;

public interface SmsService {
    void sendSms(String phoneNumber, String message);
}

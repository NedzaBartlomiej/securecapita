package pl.bartlomiej.securecapita.sms;

public interface SmsService {
    void sendSms(String coutnry_prefix, String phoneNumber, String message);
}

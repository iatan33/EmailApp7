package ua.emailclient.service.sender;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class EmailSenderFactory {

    private final Map<String, AbstractEmailSender> senders;

    public EmailSenderFactory(GmailSender gmail, UkrNetSender ukrNet, IuaSender iua) {
        this.senders = Map.of(
                "gmail.com", gmail,
                "ukr.net", ukrNet,
                "i.ua", iua
        );
    }

    public AbstractEmailSender getSenderFor(String email) {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        return senders.getOrDefault(domain, senders.get("gmail.com"));
    }
}
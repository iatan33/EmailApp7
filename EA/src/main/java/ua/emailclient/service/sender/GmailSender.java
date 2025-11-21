package ua.emailclient.service.sender;

import org.springframework.stereotype.Component;

@Component
public class GmailSender extends AbstractEmailSender {
    @Override
    protected String getSmtpHost() { return "smtp.gmail.com"; }

    @Override
    protected int getSmtpPort() { return 587; }

    @Override
    protected boolean isSslEnabled() { return true; }

    @Override
    protected String getProviderName() { return "Gmail"; }
}
package ua.emailclient.service.sender;

import org.springframework.stereotype.Component;

@Component
public class IuaSender extends AbstractEmailSender {
    @Override
    protected String getSmtpHost() { return "smtp.i.ua"; }

    @Override
    protected int getSmtpPort() { return 465; }

    @Override
    protected boolean isSslEnabled() { return true; }

    @Override
    protected String getProviderName() { return "I.UA"; }
}
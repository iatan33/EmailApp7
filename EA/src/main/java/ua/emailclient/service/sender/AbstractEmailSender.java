package ua.emailclient.service.sender;

import ua.emailclient.model.Mail;
import java.util.Properties;

public abstract class AbstractEmailSender {

    public final void sendEmail(Mail mail, String password) {
        System.out.println("=== Початок відправки листа через " + getProviderName() + " ===");

        validate(mail);

        Properties props = new Properties();
        props.put("mail.smtp.host", getSmtpHost());
        props.put("mail.smtp.port", String.valueOf(getSmtpPort()));
        props.put("mail.smtp.ssl.enable", String.valueOf(isSslEnabled()));
        props.put("mail.smtp.auth", "true");

        System.out.println("Налаштування: " + props);

        connect(mail.getSender(), password);

        transport(mail);

        disconnect();

        System.out.println("=== Лист успішно відправлено ===");
    }

    protected abstract String getSmtpHost();
    protected abstract int getSmtpPort();
    protected abstract boolean isSslEnabled();
    protected abstract String getProviderName();

    private void validate(Mail mail) {
        if (mail.getReceiver() == null || mail.getReceiver().isEmpty()) {
            throw new IllegalArgumentException("Отримувач не може бути порожнім");
        }
        System.out.println("Валідація листа пройдена.");
    }

    private void connect(String username, String password) {
        System.out.println("З'єднання з сервером " + getSmtpHost() + "...");
        System.out.println("Авторизація користувача: " + username);
    }

    private void transport(Mail mail) {
        System.out.println("Відправка контенту: '" + mail.getSubject() + "' до " + mail.getReceiver());
    }

    private void disconnect() {
        System.out.println("Відключення від сервера.");
    }
}
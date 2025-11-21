package ua.emailclient.service;

import org.springframework.stereotype.Service;
import ua.emailclient.dao.MailDAO;
import ua.emailclient.dao.UserDAO;
import ua.emailclient.model.Mail;
import ua.emailclient.model.User;
import ua.emailclient.service.sender.AbstractEmailSender;
import ua.emailclient.service.sender.EmailSenderFactory;

import java.util.List;

@Service
public class MailService {

    private final MailDAO mailDAO;
    private final UserDAO userDAO;
    private final EmailSenderFactory emailSenderFactory;

    public MailService(MailDAO mailDAO, UserDAO userDAO, EmailSenderFactory emailSenderFactory) {
        this.mailDAO = mailDAO;
        this.userDAO = userDAO;
        this.emailSenderFactory = emailSenderFactory;
    }

    public Mail sendMail(Mail mail, String senderUsername) {
        User senderUser = userDAO.findByUsername(senderUsername);
        if (senderUser == null) {
            throw new RuntimeException("Sender user not found: " + senderUsername);
        }

        AbstractEmailSender sender = emailSenderFactory.getSenderFor(senderUsername);

        String password = "user_password";
        try {
            sender.sendEmail(mail, password);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }

        Mail mailToSave = new Mail.MailBuilder(senderUsername, senderUser.getId())
                .id(mail.getId())
                .receiver(mail.getReceiver())
                .subject(mail.getSubject())
                .body(mail.getBody())
                .folder("Sent")
                .build();

        return mailDAO.save(mailToSave);
    }

    public Mail saveDraft(Mail mail, String senderUsername) {
        User senderUser = userDAO.findByUsername(senderUsername);
        if (senderUser == null) {
            throw new RuntimeException("Sender user not found: " + senderUsername);
        }
        Mail mailToSave = new Mail.MailBuilder(senderUsername, senderUser.getId())
                .id(mail.getId())
                .receiver(mail.getReceiver())
                .subject(mail.getSubject())
                .body(mail.getBody())
                .folder("Draft")
                .build();
        return mailDAO.save(mailToSave);
    }

    public Mail getMailById(int id, String username) {
        User owner = userDAO.findByUsername(username);
        if (owner == null) return null;
        return mailDAO.findByIdAndOwner(id, owner.getId());
    }

    public List<Mail> getInbox(String username) { return mailDAO.findByReceiver(username); }

    public List<Mail> getSent(String username) {
        User owner = userDAO.findByUsername(username);
        if (owner == null) throw new RuntimeException("User not found");
        return mailDAO.findByFolderAndOwner("Sent", owner.getId());
    }

    public List<Mail> getDrafts(String username) {
        User owner = userDAO.findByUsername(username);
        if (owner == null) throw new RuntimeException("User not found");
        return mailDAO.findByFolderAndOwner("Draft", owner.getId());
    }
}
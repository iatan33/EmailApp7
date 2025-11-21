package ua.emailclient.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.emailclient.model.Mail;
import ua.emailclient.service.MailService;
import java.util.List;

@Controller
@RequestMapping("/mail")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @GetMapping("/new")
    public String newMailForm(Model model, Authentication authentication) {
        Mail newMail = new Mail();
        newMail.setSender(authentication.getName());
        model.addAttribute("mail", newMail);
        return "letter";
    }

    @GetMapping("/drafts/{id}")
    public String editDraftForm(@PathVariable("id") int id, Model model, Authentication authentication) {
        Mail draft = mailService.getMailById(id, authentication.getName());

        if (draft == null || !draft.getFolder().equals("Draft")) {
            return "redirect:/home";
        }

        model.addAttribute("mail", draft);
        return "letter";
    }

    @PostMapping("/saveDraft")
    public String saveDraft(@ModelAttribute Mail mail, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            Mail savedMail = mailService.saveDraft(mail, authentication.getName());

            redirectAttributes.addFlashAttribute("message", "Чернетку успішно збережено!");
            return "redirect:/mail/drafts/" + savedMail.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Помилка при збереженні чернетки: " + e.getMessage());
            return "redirect:/mail/new";
        }
    }

    @PostMapping("/send")
    public String sendMail(@ModelAttribute Mail mail, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            mailService.sendMail(mail, authentication.getName());

            redirectAttributes.addFlashAttribute("message", "Лист успішно відправлено!");
            return "redirect:/home";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Помилка при відправленні листа: " + e.getMessage());
            return "redirect:/mail/new";
        }
    }

    @GetMapping("/inbox")
    public String viewInbox(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Mail> inboxMails = mailService.getInbox(username);
        model.addAttribute("mails", inboxMails);
        model.addAttribute("folderName", "Вхідні");
        model.addAttribute("folderType", "inbox");
        return "mailbox";
    }

    @GetMapping("/sent")
    public String viewSent(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Mail> sentMails = mailService.getSent(username);
        model.addAttribute("mails", sentMails);
        model.addAttribute("folderName", "Відправлені");
        model.addAttribute("folderType", "sent");
        return "mailbox";
    }

    @GetMapping("/drafts")
    public String viewDraftsList(Model model, Authentication authentication) {
        String username = authentication.getName();
        List<Mail> draftMails = mailService.getDrafts(username);
        model.addAttribute("mails", draftMails);
        model.addAttribute("folderName", "Чернетки");
        model.addAttribute("folderType", "drafts");
        return "mailbox";
    }

    @GetMapping("/{id}")
    public String readMail(@PathVariable("id") int id, Model model, Authentication authentication) {
        Mail mail = mailService.getMailById(id, authentication.getName());

        if (mail == null) {
            return "redirect:/home";
        }

        if (mail.getFolder().equals("Draft")) {
            return "redirect:/mail/drafts/" + mail.getId();
        }

        model.addAttribute("mail", mail);
        return "read";
    }
}
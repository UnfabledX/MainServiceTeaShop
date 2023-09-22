package com.leka.teashop.service.impl;

import com.leka.teashop.email.EmailContext;
import com.leka.teashop.email.EmailNotificationType;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;

import static com.leka.teashop.email.EmailNotificationType.NEW_ORDER_FOR_ADMIN;
import static com.leka.teashop.email.EmailNotificationType.ORDER_COMPLETION_FOR_USER;
import static com.leka.teashop.email.EmailNotificationType.RENEW_LINK;
import static com.leka.teashop.email.EmailNotificationType.VERIFICATION;

@Service
public class EmailServiceImpl implements EmailService {

    private final String adminEmail;
    private final String emailFrom;
    private final Integer timeToLive;
    private final JavaMailSender mailSender;
    private final Map<EmailNotificationType, EmailContext> emailContext;

    public EmailServiceImpl(@Value("${source.mail.address}") String emailFrom,
                            @Value("${email.user-registered.time-to-live}") Integer timeToLive,
                            @Value("${admin.email}") String adminEmail,
                            Map<EmailNotificationType, EmailContext> emailContext,
                            JavaMailSender mailSender) {
        this.emailFrom = emailFrom;
        this.timeToLive = timeToLive;
        this.adminEmail = adminEmail;
        this.mailSender = mailSender;
        this.emailContext = emailContext;
    }

    @Override
    public void sendVerificationEmail(User user, String confirmationUrl) {
        Locale locale = LocaleContextHolder.getLocale();
        EmailContext context = emailContext.get(VERIFICATION);
        String emailBody = context.getEmailBody(locale, user, confirmationUrl, timeToLive);

        MimeMessagePreparator prep = getMimeMessagePreparator(user, emailBody,
                context.getSubject(locale));

        mailSender.send(prep);
    }

    @Override
    public void sendOrderDetailsEmail(User currentUser) {
        Locale locale = LocaleContextHolder.getLocale();
        EmailContext context = emailContext.get(ORDER_COMPLETION_FOR_USER);

        String emailBody = context.getEmailBody(locale, currentUser);
        String emailSubject =  context.getSubject(locale);

        MimeMessagePreparator prep = getMimeMessagePreparator(currentUser, emailBody,
                emailSubject);

        mailSender.send(prep);
    }

    @Override
    public void sendNewOrderForAdminEmail(User currentUser) {
        Locale locale = LocaleContextHolder.getLocale();
        EmailContext context = emailContext.get(NEW_ORDER_FOR_ADMIN);

        String emailBody = context.getEmailBody(locale, currentUser);
        String emailSubject =  context.getSubject(locale);

        MimeMessagePreparator prep = getMimeMessagePreparator(adminEmail, emailBody,
                emailSubject);
        if (prep != null) {
            mailSender.send(prep);
        }
    }

    @Override
    public void sendRenewLinkEmail(User user, String confirmationUrl) {
        Locale locale = LocaleContextHolder.getLocale();
        EmailContext context = emailContext.get(RENEW_LINK);
        String emailBody = context.getEmailBody(locale, user, confirmationUrl, timeToLive);

        MimeMessagePreparator prep = getMimeMessagePreparator(user, emailBody,
                context.getSubject(locale));

        mailSender.send(prep);
    }

    private MimeMessagePreparator getMimeMessagePreparator(Object emailHolder, String emailBody,
                                                           String subject) {
        StringWriter writer = new StringWriter();
        writer.write(emailBody);

        MimeMessagePreparator messagePreparator = null;
        if (emailHolder instanceof String email) {
            messagePreparator = getMimeMessagePreparator(email, subject, writer);
        } else if (emailHolder instanceof User currentUser){
            messagePreparator = getMimeMessagePreparator(currentUser.getEmail(), subject, writer);
        }
        return messagePreparator;
    }

    private MimeMessagePreparator getMimeMessagePreparator(String email, String subject, StringWriter writer) {
        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(email);
            helper.setFrom(emailFrom);
            helper.setSubject(subject);
            helper.setText(writer.toString(), true);
        };
    }
}

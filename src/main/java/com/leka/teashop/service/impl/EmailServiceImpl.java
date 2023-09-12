package com.leka.teashop.service.impl;

import com.leka.teashop.email.EmailContext;
import com.leka.teashop.email.OrderCompletionEmail;
import com.leka.teashop.email.VerificationEmail;
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

@Service
public class EmailServiceImpl implements EmailService {

    private final String emailFrom;
    private final Integer timeToLive;
    private final JavaMailSender mailSender;
    private final OrderCompletionEmail orderCompletionEmail;

    public EmailServiceImpl(@Value("${source.mail.address}") String emailFrom,
                            @Value("${email.user-registered.time-to-live}") Integer timeToLive,
                            OrderCompletionEmail orderCompletionEmail,
                            JavaMailSender mailSender) {
        this.emailFrom = emailFrom;
        this.timeToLive = timeToLive;
        this.mailSender = mailSender;
        this.orderCompletionEmail = orderCompletionEmail;
    }

    @Override
    public void sendVerificationEmail(User user, String confirmationUrl) {
        Locale locale = LocaleContextHolder.getLocale();
        EmailContext emailContext = new VerificationEmail();
        String emailBody = emailContext.getEmailBody(locale, user, confirmationUrl, timeToLive);

        MimeMessagePreparator prep = getMimeMessagePreparator(user, emailBody,
                emailContext.getSubject(locale));

        mailSender.send(prep);
    }

    @Override
    public void sendOrderDetailsEmail(User currentUser) {
        Locale locale = LocaleContextHolder.getLocale();
        String emailBody = orderCompletionEmail.getEmailBody(locale, currentUser);

        MimeMessagePreparator prep = getMimeMessagePreparator(currentUser, emailBody,
                orderCompletionEmail.getSubject(locale));

        mailSender.send(prep);
    }


    private MimeMessagePreparator getMimeMessagePreparator(User currentUser, String emailBody,
                                                           String subject) {
        StringWriter writer = new StringWriter();
        writer.write(emailBody);
        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(currentUser.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject(subject);
            helper.setText(writer.toString(), true);
        };
    }
}

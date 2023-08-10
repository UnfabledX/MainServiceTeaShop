package com.leka.teashop.service.impl;

import com.leka.teashop.email.EmailBody_EN;
import com.leka.teashop.email.EmailBody_UKR;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class EmailServiceImpl implements EmailService {

    private final String emailFrom;
    private final Integer timeToLive;
    private final JavaMailSender mailSender;

    public EmailServiceImpl(@Value("${source.mail.address}") String emailFrom,
                            @Value("${email.user-registered.time-to-live}") Integer timeToLive,
                            JavaMailSender mailSender) {
        this.emailFrom = emailFrom;
        this.timeToLive = timeToLive;
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(User user, String confirmationUrl, String locale) {
        StringWriter writer = new StringWriter();
        boolean isEnglish = "en".equalsIgnoreCase(locale);
        String emailBody = isEnglish ?
                EmailBody_EN.getVerificationEmailBody(user, confirmationUrl, timeToLive) :
                EmailBody_UKR.getVerificationEmailBody(user, confirmationUrl, timeToLive);
        writer.write(emailBody);
        MimeMessagePreparator prep = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(user.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject(isEnglish ?
                    EmailBody_EN.SUBJECT_VERIFICATION_EMAIL_EN :
                    EmailBody_UKR.SUBJECT_VERIFICATION_EMAIL_UKR);
            helper.setText(writer.toString(), true);
        };
        mailSender.send(prep);
    }

//    public void sendPasswordResetVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
//        String subject = "Password Reset Request Verification";
//        String senderName = "Users Verification Service";
//        String mailContent = "<p> Hi, " + user.getFirstName() + ", </p>" +
//                "<p><b>You recently requested to reset your password,</b>" + "" +
//                "Please, follow the link below to complete the action.</p>" +
//                "<a href=\"" + url + "\">Reset password</a>" +
//                "<p> Users Registration Portal Service";
//        emailMessage(subject, senderName, mailContent, mailSender, user);
//    }
}

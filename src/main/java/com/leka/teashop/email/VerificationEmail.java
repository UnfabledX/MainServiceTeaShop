package com.leka.teashop.email;

import com.leka.teashop.model.User;

import java.util.Locale;

import static com.leka.teashop.email.utils.EmailBodies.VERIFICATION_EMAIL_EN;
import static com.leka.teashop.email.utils.EmailBodies.VERIFICATION_EMAIL_UKR;
import static com.leka.teashop.email.utils.Subjects.SUBJECT_VERIFICATION_EMAIL_EN;
import static com.leka.teashop.email.utils.Subjects.SUBJECT_VERIFICATION_EMAIL_UKR;
import static com.leka.teashop.email.utils.TimeConverter.convert;

public class VerificationEmail implements EmailContext{

    @Override
    public String getSubject(Locale locale) {
        String subject = null;
        switch (locale.getLanguage()) {
            case "en" -> subject = SUBJECT_VERIFICATION_EMAIL_EN;
            case "ukr" -> subject = SUBJECT_VERIFICATION_EMAIL_UKR;
        }
        return subject;
    }

    @Override
    public String getEmailBody(Object ... parameters) {
        String emailBody = null;
        Locale locale = (Locale) parameters[0];
        switch (locale.getLanguage()) {
            case "en" -> {
                User user = (User) parameters[1];
                String link = (String) parameters[2];
                Integer timeToLive = (Integer) parameters[3];
                String creationTime = convert(user.getCreatedAt());
                emailBody = VERIFICATION_EMAIL_EN.formatted(user.getUsername(), user.getEmail(),
                        creationTime, link, timeToLive);
            }
            case "ukr" -> {
                User user = (User) parameters[1];
                String link = (String) parameters[2];
                Integer timeToLive = (Integer) parameters[3];
                String creationTime = convert(user.getCreatedAt());
                emailBody = VERIFICATION_EMAIL_UKR.formatted(user.getUsername(), user.getEmail(),
                        creationTime, link, timeToLive);
            }
        }
        return emailBody;
    }
}

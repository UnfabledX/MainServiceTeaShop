package com.leka.teashop.event.listener;

import com.leka.teashop.event.RegistrationEmailEvent;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationEmailEventListener implements ApplicationListener<RegistrationEmailEvent> {

    private final EmailService emailService;

    @Override
    public void onApplicationEvent(RegistrationEmailEvent event) {
        User user = event.getUser();
        String verificationToken = user.getVerificationToken();
        String confirmationUrl = event.getConfirmationUrl() + "/verifyEmail?token=" + verificationToken;
        emailService.sendVerificationEmail(user, confirmationUrl);
    }
}

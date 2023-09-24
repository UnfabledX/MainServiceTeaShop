package com.leka.teashop.event.listener;

import com.leka.teashop.event.ResetPasswordEmailEvent;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResetPasswordEmailEventListener {

    private final EmailService emailService;

    @EventListener
    public void onApplicationEvent(ResetPasswordEmailEvent event) {
        User user = event.getUser();
        String verificationToken = user.getVerificationToken();
        String confirmationUrl = event.getConfirmationUrl() + "/confirmReset?token=" + verificationToken;
        emailService.sendResetPasswordEmail(user, confirmationUrl);
    }
}

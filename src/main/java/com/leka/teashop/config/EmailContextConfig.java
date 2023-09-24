package com.leka.teashop.config;

import com.leka.teashop.email.EmailContext;
import com.leka.teashop.email.EmailNotificationType;
import com.leka.teashop.email.NewOrderForAdminEmail;
import com.leka.teashop.email.OrderCompletionEmail;
import com.leka.teashop.email.RenewLinkEmail;
import com.leka.teashop.email.ResetPasswordEmail;
import com.leka.teashop.email.VerificationEmail;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class EmailContextConfig {

    private final VerificationEmail verificationEmail;
    private final OrderCompletionEmail orderCompletionEmail;
    private final NewOrderForAdminEmail newOrderForAdminEmail;
    private final RenewLinkEmail renewLinkEmail;
    private final ResetPasswordEmail resetPasswordEmail;

    @Bean(name = "emailContext")
    public Map<EmailNotificationType, EmailContext> getEmailContext() {
        return Map.of(
                EmailNotificationType.VERIFICATION,                 verificationEmail,
                EmailNotificationType.ORDER_COMPLETION_FOR_USER,    orderCompletionEmail,
                EmailNotificationType.NEW_ORDER_FOR_ADMIN,          newOrderForAdminEmail,
                EmailNotificationType.RENEW_LINK,                   renewLinkEmail,
                EmailNotificationType.RESET_PASSWORD,               resetPasswordEmail
        );
    }
}

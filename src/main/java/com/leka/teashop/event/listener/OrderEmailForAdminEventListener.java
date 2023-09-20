package com.leka.teashop.event.listener;

import com.leka.teashop.event.OrderEmailForAdminEvent;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEmailForAdminEventListener {

    private final EmailService emailService;

    @EventListener
    public void onApplicationEvent(OrderEmailForAdminEvent event) {
        User user = event.getUser();
        emailService.sendNewOrderForAdminEmail(user);
    }
}

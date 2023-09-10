package com.leka.teashop.event.listener;

import com.leka.teashop.event.OrderEmailEvent;
import com.leka.teashop.model.User;
import com.leka.teashop.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEmailEventListener implements ApplicationListener<OrderEmailEvent> {

    private final EmailService emailService;

    @Override
    public void onApplicationEvent(OrderEmailEvent event) {
        User user = event.getUser();
        emailService.sendOrderDetailsEmail(user);
    }
}

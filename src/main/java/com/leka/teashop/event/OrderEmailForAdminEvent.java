package com.leka.teashop.event;

import com.leka.teashop.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OrderEmailForAdminEvent extends ApplicationEvent {
    private User user;

    public OrderEmailForAdminEvent(User user) {
        super(user);
        this.user = user;
    }
}

package com.leka.teashop.event;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import com.leka.teashop.model.User;

import java.util.Locale;

@Getter
@Setter
public class RegistrationEmailEvent extends ApplicationEvent {
    private User user;
    private String confirmationUrl;

    public RegistrationEmailEvent(User user, String confirmationUrl) {
        super(user);
        this.user = user;
        this.confirmationUrl = confirmationUrl;
    }
}

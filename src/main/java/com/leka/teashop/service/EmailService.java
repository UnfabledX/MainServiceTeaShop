package com.leka.teashop.service;


import com.leka.teashop.model.User;

public interface EmailService {

    void sendVerificationEmail(User user, String confirmationUrl);

    void sendOrderDetailsEmail(User currentUser);

    void sendNewOrderForAdminEmail(User user);

    void sendRenewLinkEmail(User user, String confirmationUrl);

    void sendResetPasswordEmail(User user, String confirmationUrl);
}

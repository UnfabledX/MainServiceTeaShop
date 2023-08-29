package com.leka.teashop.exception.handler;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.model.AccountStatus;
import com.leka.teashop.model.User;
import com.leka.teashop.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        String contextPath = request.getContextPath(); // in our case /teashop
        String username = request.getParameter("username");
        if (exception instanceof AccountStatusException) {
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new NotFoundException("The user is not found!"));
            if (user.getAccountStatus() == AccountStatus.INACTIVE) {
                response.sendRedirect(contextPath + "/login?userInactive");
            }
            if (user.getAccountStatus() == AccountStatus.BANNED){
                response.sendRedirect(contextPath + "/login?userBanned");
            }
        } else {
            response.sendRedirect(contextPath + "/login?badCredentials");
        }
    }
}

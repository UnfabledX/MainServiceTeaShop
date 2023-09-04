package com.leka.teashop.handler;

import com.leka.teashop.model.User;
import com.leka.teashop.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler extends SimpleUrlLogoutSuccessHandler {

    private final OrderService orderService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {
        User user = null;
        if (authentication instanceof UsernamePasswordAuthenticationToken token) {
            user = (User) token.getPrincipal();
        }
        orderService.deleteStartedOrdersWhenLogout(user);
        response.sendRedirect(request.getContextPath() + "/login");
        super.onLogoutSuccess(request, response, authentication);
    }
}

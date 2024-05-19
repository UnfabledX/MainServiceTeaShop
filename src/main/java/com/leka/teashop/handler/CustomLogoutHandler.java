package com.leka.teashop.handler;

import com.leka.teashop.model.User;
import com.leka.teashop.service.OrderService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;

@Log4j2
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
        try {
            orderService.deleteStartedOrdersWhenLogout(user);
        } catch (WebClientResponseException exception) {
            log.error("Error while deleting started orders when logout. OrderService is unavailable ", exception);
        } finally {
            response.sendRedirect(request.getContextPath() + "/login");
            super.onLogoutSuccess(request, response, authentication);
        }
    }
}

package com.leka.teashop.controller;

import com.leka.teashop.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Collection;
import java.util.Locale;

@Controller
@RequiredArgsConstructor
public class BlogController {


    @Value("${blog.external.url}")
    private String blogUrl;

    private final JwtService jwtService;

    @GetMapping("/blog")
    public RedirectView blog() {
        Locale locale = LocaleContextHolder.getLocale();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String tokenParam = null;
        if (auth instanceof UsernamePasswordAuthenticationToken token) {
            Collection<GrantedAuthority> authorities = token.getAuthorities();
            if (authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                tokenParam = jwtService.generateAdminToken(auth);
            }
        }
        if (tokenParam != null) {
            return new RedirectView(blogUrl + "/main?token=" + tokenParam + "&lang=" + locale.getLanguage());
        } else {
            return new RedirectView(blogUrl + "/main?lang=" + locale.getLanguage());
        }
    }
}

package com.leka.teashop.security;

import com.leka.teashop.exception.handler.CustomAuthenticationFailureHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler loginFailureHandler;
    private final AccessDeniedHandler accessDeniedHandler;

    /**
     * 1. Instead of providing defaultSuccessUrl we instruct spring security to return
     * to the previous visited page after successful user login.
     * 2.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers(
                                "/",
                                "/showAllProductsForSale",
                                "/login",
                                "/register",
                                "/verifyEmail",
                                "/image/*",
                                "/error"
                        ).permitAll()
                        .requestMatchers(PathRequest.toStaticResources()
                                .atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/allProducts",
                                "/product",
                                "/delete/*",
                                "/edit/*",
                                "/updateProduct/*",
                                "/adminPanel"
                        ).hasAnyAuthority("ROLE_ADMIN")
                        .anyRequest()
                        .authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/")
                        .failureHandler(loginFailureHandler)
                        .permitAll())
                .logout((logout) -> logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .permitAll())
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
}

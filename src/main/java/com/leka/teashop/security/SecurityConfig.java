package com.leka.teashop.security;

import com.leka.teashop.handler.CustomAccessDeniedHandler;
import com.leka.teashop.handler.CustomAuthenticationFailureHandler;
import com.leka.teashop.handler.CustomLogoutHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomAuthenticationFailureHandler loginFailureHandler;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final CustomLogoutHandler customLogoutHandler;

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
                                "/error",
                                "/renew",
                                "/resetPassword",
                                "/confirmReset",
                                "/applyNewPassword",
                                "/load"
                        ).permitAll()
                        .requestMatchers(PathRequest.toStaticResources()
                                .atCommonLocations()).permitAll()
                        .requestMatchers(
                                "/allProducts",
                                "/product",
                                "/delete/*",
                                "/editProduct/*",
                                "/updateProduct/*",
                                "/adminPanel",
                                "/allUsers",
                                "/editAddress/*",
                                "/editAddress",
                                "/createAddress/*",
                                "/createDeliveryAddress",
                                "/editUser/*",
                                "/allOrders",
                                "/changeOrderStatus/*",
                                "/ordersInProcess",
                                "/changeInProgressStatus/*"
                        ).hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers(
                                "/settings",
                                "/addToOrder",
                                "/logout",
                                "/cart",
                                "/decreaseCounter/*",
                                "/increaseCounter/*",
                                "/deleteFromCart/*",
                                "/deliveryOptions",
                                "/userOrders"
                                //some more
                        ).hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
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
                        .logoutSuccessHandler(customLogoutHandler))
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.accessDeniedHandler(accessDeniedHandler));
        return http.build();
    }
}

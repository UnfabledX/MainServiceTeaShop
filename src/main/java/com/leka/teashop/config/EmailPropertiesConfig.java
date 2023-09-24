package com.leka.teashop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "email")
public class EmailPropertiesConfig {

    private Integer timeToLiveForRegistrationLink;
    private Long timeToLiveForPasswordResetLink;
    private String sourceAddress;
    private String adminEmail;
}

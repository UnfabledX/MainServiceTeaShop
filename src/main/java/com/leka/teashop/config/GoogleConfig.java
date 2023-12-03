package com.leka.teashop.config;

import com.google.api.services.drive.Drive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.leka.teashop.config.GoogleAuthorizeUtil.HTTP_TRANSPORT;
import static com.leka.teashop.config.GoogleAuthorizeUtil.JSON_FACTORY;
import static com.leka.teashop.config.GoogleAuthorizeUtil.credentials;

@Configuration
@EnableConfigurationProperties(GoogleConfig.GoogleProperties.class)
public class GoogleConfig {

    @ConfigurationProperties(prefix = "google.properties")
    public record GoogleProperties(String spreadsheetId,
                                   String folderId,
                                   String rangeOfColumns) {
    }

    @Bean
    public Drive getGoogleDrive() throws IOException {
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials())
                .setApplicationName("Teashop")
                .build();
    }
}

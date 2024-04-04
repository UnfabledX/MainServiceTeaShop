package com.leka.teashop.config;

import com.google.api.services.drive.Drive;
import com.google.api.services.sheets.v4.Sheets;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.leka.teashop.config.GoogleAuthorizeUtil.HTTP_TRANSPORT;
import static com.leka.teashop.config.GoogleAuthorizeUtil.JSON_FACTORY;
import static com.leka.teashop.config.GoogleAuthorizeUtil.credentials;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(GoogleConfig.GoogleProperties.class)
public class GoogleConfig {


    private final GoogleProperties properties;

    @ConfigurationProperties(prefix = "google.properties")
    public record GoogleProperties(String spreadsheetId,
                                   String folderId,
                                   String rangeOfColumns,
                                   String appName) {
    }

    @Bean
    public Drive getGoogleDrive() throws IOException {
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials())
                .setApplicationName(properties.appName())
                .build();
    }

    @Bean
    public Sheets getGoogleSheets() throws IOException {
        return new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials())
                .setApplicationName(properties.appName())
                .build();
    }
}

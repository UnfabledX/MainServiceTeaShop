package com.leka.teashop.email.utils;

import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;


public class TimeConverter {

    private static final String timePattern_EN = "EEEE, MMM dd, yyyy HH:mm:ss";
    private static final String timePattern_UKR = "dd.MM.yyyy HH:mm:ss";

    public static String convert(LocalDateTime localDateTime) {
        Locale locale = LocaleContextHolder.getLocale();
        DateTimeFormatter dateTimeFormatter;
        if (locale.getLanguage().equals("ukr")) {
            dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern_UKR, locale);
        } else {
            dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern_EN, locale);
        }
        return dateTimeFormatter.format(localDateTime);
    }
}

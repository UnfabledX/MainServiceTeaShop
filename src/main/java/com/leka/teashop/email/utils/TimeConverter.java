package com.leka.teashop.email.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class TimeConverter {

    private static final String timePattern = "EEEE, MMM dd, yyyy HH:mm:ss";

    public static String convert(LocalDateTime localDateTime){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(timePattern);
        return dateTimeFormatter.format(ZonedDateTime.of(localDateTime, ZoneId.systemDefault()));
    }
}

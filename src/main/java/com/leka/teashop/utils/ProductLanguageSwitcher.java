package com.leka.teashop.utils;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component(value = "productLanguageSwitcher")
public class ProductLanguageSwitcher {

    public String defineLanguageOf(String description) {
        Locale locale = LocaleContextHolder.getLocale();
        if (description.contains("#&#")) {
            String[] descriptions = description.split("#&#");
            switch (locale.getLanguage()) {
                case "ukr" -> description = descriptions[0];
                case "en" -> description = descriptions[1];
            }
        }
        return description;
    }
}

package com.leka.teashop.email;

import java.util.Locale;

public interface EmailContext {

    String getSubject(Locale locale);

    String getEmailBody(Object ... parameters);
}

package com.leka.teashop.email;

import com.leka.teashop.model.User;

import static com.leka.teashop.email.utils.TimeConverter.convert;

public class EmailBody_UKR {
    public static final String SUBJECT_VERIFICATION_EMAIL_UKR = "Ваша Реєстрація в онлайн магазині NotkaTea!";
    public static final String VERIFICATION_EMAIL_UKR = """
            <html lang="en">
            <head>
                <title>Сповіщення про реєстрацію в онлайн магазині NotkaTea</title>
            </head>
            <body bgcolor="#faf7f1">
            <div style="text-align: -webkit-left;">
                <h3>Мої вітання, %s, Ви щойно зареєструвалися за електронною поштою:<br/>
                    %s <br/>
                    о %s
                </h3>
                <h4>
                    Залишився останній крок - натисніть на посилання <a href="%s">Завершити реєстацію</a><br/>
                    щоб підтвердити ваш акаунт.<br/>
                    Лише зазначу, що посилання буде актуальним тільки %d хвилин.
                </h4>

                <h4><i>З найкращими побажаннями, <br/>
                    команда онлайн магазину NotkaTea</i> </h4>
            </div>
            </body>
            </html>
            """;

    public static String getVerificationEmailBody(User user, String link, Integer timeToLive) {
        String creationTime = convert(user.getCreatedAt());
        return VERIFICATION_EMAIL_UKR.formatted(user.getUsername(), user.getEmail(), creationTime, link, timeToLive);
    }


}

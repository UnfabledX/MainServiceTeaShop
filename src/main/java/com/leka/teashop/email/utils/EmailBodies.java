package com.leka.teashop.email.utils;

public class EmailBodies {

    public static final String VERIFICATION_EMAIL_EN = """
            <html lang="en">
            <head>
                <title>NotkaTea user registration notification</title>
            </head>
            <body bgcolor="#faf7f1">
            <div style="text-align: -webkit-left;">
                <h3>Hello %s, you've just registered your account with the email:<br/>
                    %s <br/>
                    at %s
                </h3>
                <h4>
                    One last step, just click here on a <a href="%s">Registration Link</a><br/>
                    to verify your account.<br/>
                    Please note, the above link is only valid for %d minutes.
                </h4>

                <h4><i>Best regards, <br/>
                    your NotkaTea Team</i> </h4>
            </div>
            </body>
            </html>
            """;


    public static final String VERIFICATION_EMAIL_UKR = """
            <html lang="en">
            <head>
                <title>Сповіщення про реєстрацію в онлайн магазині NotkaTea</title>
            </head>
            <body bgcolor="#faf7f1">
            <div style="text-align: -webkit-left;">
                <h3>Мої вітання, %s, Ви щойно зареєструвалися за електронною поштою:<br/>
                    %s <br/>
                    Час реєстрації - %s
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
}

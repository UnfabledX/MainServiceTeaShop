package com.leka.teashop.email;

import com.leka.teashop.model.User;

import static com.leka.teashop.email.utils.TimeConverter.convert;

public class EmailBody_EN {
    public static final String SUBJECT_VERIFICATION_EMAIL_EN = "Your registration for NotkaTea shop!";
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

    public static String getVerificationEmailBody(User user, String link, Integer timeToLive) {
        String creationTime = convert(user.getCreatedAt());
        return VERIFICATION_EMAIL_EN.formatted(user.getUsername(), user.getEmail(), creationTime, link, timeToLive);
    }


}

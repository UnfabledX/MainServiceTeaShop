package com.leka.teashop.email;

import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

import static com.leka.teashop.email.utils.Subjects.SUBJECT_VERIFICATION_EMAIL_EN;
import static com.leka.teashop.email.utils.Subjects.SUBJECT_VERIFICATION_EMAIL_UKR;
import static org.thymeleaf.spring6.expression.ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME;

@Component
@RequiredArgsConstructor
public class VerificationEmail implements EmailContext{

    private final UserMapperImpl userMapper;
    private final TemplateEngine emailTemplateEngine;
    private final ApplicationContext applicationContext;

    @Override
    public String getSubject(Locale locale) {
        String subject = null;
        switch (locale.getLanguage()) {
            case "en" -> subject = SUBJECT_VERIFICATION_EMAIL_EN;
            case "ukr" -> subject = SUBJECT_VERIFICATION_EMAIL_UKR;
        }
        return subject;
    }

    @Override
    public String getEmailBody(Object ... parameters) {
        Locale locale = (Locale) parameters[0];
        User user = (User) parameters[1];
        String link = (String) parameters[2];
        Integer timeToLive = (Integer) parameters[3];
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String createdAt = user.getCreatedAt().format(dateTimeFormatter);

        ThymeleafEvaluationContext thymeleafEvaluationContext = new ThymeleafEvaluationContext(applicationContext, null);
        Map<String, Object> variables = Map.of(
                "user", userMapper.toDto(user),
                "link", link,
                "timeToLive", timeToLive,
                "createdAt", createdAt,
                THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, thymeleafEvaluationContext
        );

        Context context = new Context(locale);
        context.setVariables(variables);

        return emailTemplateEngine.process("user-verification-email.html", context);
    }
}

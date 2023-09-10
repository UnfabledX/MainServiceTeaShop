package com.leka.teashop.service.impl;

import com.leka.teashop.email.VerificationEmailBody_EN;
import com.leka.teashop.email.VerificationEmailBody_UKR;
import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.EmailService;
import com.leka.teashop.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import static com.leka.teashop.email.Subjects.SUBJECT_ORDER_COMPLETION_EMAIL_EN;
import static com.leka.teashop.email.Subjects.SUBJECT_ORDER_COMPLETION_EMAIL_UKR;
import static com.leka.teashop.email.Subjects.SUBJECT_VERIFICATION_EMAIL_EN;
import static com.leka.teashop.email.Subjects.SUBJECT_VERIFICATION_EMAIL_UKR;

@Service
public class EmailServiceImpl implements EmailService {

    private final String emailFrom;
    private final Integer timeToLive;
    private final JavaMailSender mailSender;
    private final ProductService productService;
    private final ProductMapper productMapper;
    private final UserMapperImpl userMapper;
    private final AddressOfDeliveryMapper deliveryMapper;
    private final TemplateEngine emailTemplateEngine;
    private final ApplicationContext applicationContext;

    public EmailServiceImpl(@Value("${source.mail.address}") String emailFrom,
                            @Value("${email.user-registered.time-to-live}") Integer timeToLive,
                            ProductService productService,
                            ProductMapper productMapper,
                            AddressOfDeliveryMapper deliveryMapper,
                            UserMapperImpl userMapper,
                            TemplateEngine emailTemplateEngine,
                            ApplicationContext applicationContext,
                            JavaMailSender mailSender) {
        this.emailFrom = emailFrom;
        this.timeToLive = timeToLive;
        this.productService = productService;
        this.productMapper = productMapper;
        this.mailSender = mailSender;
        this.deliveryMapper = deliveryMapper;
        this.userMapper = userMapper;
        this.emailTemplateEngine = emailTemplateEngine;
        this.applicationContext = applicationContext;
    }

    @Override
    public void sendVerificationEmail(User user, String confirmationUrl) {
        StringWriter writer = new StringWriter();
        String emailBody = isEnglish() ?
                VerificationEmailBody_EN.getVerificationEmailBody(user, confirmationUrl, timeToLive) :
                VerificationEmailBody_UKR.getVerificationEmailBody(user, confirmationUrl, timeToLive);
        writer.write(emailBody);
        MimeMessagePreparator prep = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(user.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject(isEnglish() ?
                    SUBJECT_VERIFICATION_EMAIL_EN :
                    SUBJECT_VERIFICATION_EMAIL_UKR);
            helper.setText(writer.toString(), true);
        };
        mailSender.send(prep);
    }

    @Override
    public void sendOrderDetailsEmail(User currentUser) {
        OrderDto orderDto = currentUser.getCurrentOrderDto();
        Map<Long, Integer> productIdAndCount = orderDto.getProductIdAndCount();
        List<ProductDto> products = productIdAndCount.keySet()
                .stream()
                .map(productService::findById)
                .map(productMapper::toDto)
                .toList();
        AddressOfDelivery addressOfDelivery = currentUser.getAddressOfDelivery();
        ThymeleafEvaluationContext thymeleafEvaluationContext = new ThymeleafEvaluationContext(applicationContext, null);
        Map<String, Object> variables = Map.of(
                "user", userMapper.toDto(currentUser),
                "products", products,
                "productIdAndCount", productIdAndCount,
                "address", deliveryMapper.toDto(addressOfDelivery),
                ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, thymeleafEvaluationContext
        );
        Context context = new Context(LocaleContextHolder.getLocale());
        context.setVariables(variables);

        String htmlContent = emailTemplateEngine.process("order-details.html", context);
        MimeMessagePreparator prep = getMimeMessagePreparator(currentUser, htmlContent);

        mailSender.send(prep);
    }

    private MimeMessagePreparator getMimeMessagePreparator(User currentUser, String htmlContent) {
        StringWriter writer = new StringWriter();
        writer.write(htmlContent);

        return mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
            helper.setTo(currentUser.getEmail());
            helper.setFrom(emailFrom);
            helper.setSubject(isEnglish() ?
                    SUBJECT_ORDER_COMPLETION_EMAIL_EN :
                    SUBJECT_ORDER_COMPLETION_EMAIL_UKR);
            helper.setText(writer.toString(), true);
        };
    }

    private boolean isEnglish() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return "en".equalsIgnoreCase(language);
    }
}

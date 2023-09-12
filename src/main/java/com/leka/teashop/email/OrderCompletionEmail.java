package com.leka.teashop.email;

import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.leka.teashop.email.utils.Subjects.SUBJECT_ORDER_COMPLETION_EMAIL_EN;
import static com.leka.teashop.email.utils.Subjects.SUBJECT_ORDER_COMPLETION_EMAIL_UKR;

@Component
@RequiredArgsConstructor
public class OrderCompletionEmail implements EmailContext {

    private final ProductService productService;
    private final ProductMapper productMapper;
    private final ApplicationContext applicationContext;
    private final UserMapperImpl userMapper;
    private final AddressOfDeliveryMapper deliveryMapper;
    private final TemplateEngine emailTemplateEngine;

    @Override
    public String getSubject(Locale locale) {
        String subject;
        switch (locale.getLanguage()) {
            case "en" -> subject = SUBJECT_ORDER_COMPLETION_EMAIL_EN;
            case "ukr" -> subject = SUBJECT_ORDER_COMPLETION_EMAIL_UKR;
            default -> subject = "Default subject";
        }
        return subject;
    }

    @Override
    public String getEmailBody(Object... parameters) {
        Locale locale = (Locale) parameters[0];
        User currentUser = (User) parameters[1];

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
        Context context = new Context(locale);
        context.setVariables(variables);

        return emailTemplateEngine.process("order-details.html", context);
    }
}

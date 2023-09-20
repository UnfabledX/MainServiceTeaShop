package com.leka.teashop.email;

import com.leka.teashop.mapper.AddressOfDeliveryMapper;
import com.leka.teashop.mapper.ProductMapper;
import com.leka.teashop.mapper.UserMapperImpl;
import com.leka.teashop.model.AddressOfDelivery;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.ProductService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.expression.ThymeleafEvaluationContext;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.leka.teashop.email.utils.Subjects.SUBJECT_NEW_ORDER_EMAIL_EN;
import static com.leka.teashop.email.utils.Subjects.SUBJECT_NEW_ORDER_EMAIL_UKR;
import static org.thymeleaf.spring6.expression.ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME;

@Component
@RequiredArgsConstructor
public class NewOrderForAdminEmail implements EmailContext {

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
            case "en" -> subject = SUBJECT_NEW_ORDER_EMAIL_EN;
            case "ukr" -> subject = SUBJECT_NEW_ORDER_EMAIL_UKR;
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
        List<ProductDto> products = getProductDtos(productIdAndCount);
        AddressOfDelivery addressOfDelivery = currentUser.getAddressOfDelivery();
        ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext, null);
        String appContextUrl = getAppContextUrl();

        String changeOrderStatusToFulfilled = appContextUrl + "/changeInProgressStatus/" +
                                              orderDto.getId() + "?status=FULFILLED";
        String ordersInProcess = appContextUrl + "/ordersInProcess";

        Map<String, Object> variables = Map.of(
                "user", userMapper.toDto(currentUser),
                "products", products,
                "productIdAndCount", productIdAndCount,
                "address", deliveryMapper.toDto(addressOfDelivery),
                "changeOrderStatusToFulfilled", changeOrderStatusToFulfilled,
                "ordersInProcess", ordersInProcess,
                THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME, evaluationContext
        );

        Context context = new Context(locale);
        context.setVariables(variables);

        return emailTemplateEngine.process("new-order-from-user.html", context);
    }

    private static String getAppContextUrl() {
        HttpServletRequest curRequest =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                        .getRequest();
        return curRequest.getRequestURL()
                .toString().replace(curRequest.getServletPath(), "");
    }

    private List<ProductDto> getProductDtos(Map<Long, Integer> productIdAndCount) {
        return productIdAndCount.keySet()
                .stream()
                .map(productService::findById)
                .map(productMapper::toDto)
                .toList();
    }


}

package com.leka.teashop.utils;

import com.leka.teashop.model.Product;
import com.leka.teashop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component(value = "priceCalculator")
@RequiredArgsConstructor
public class PriceCalculator {

    private final ProductService productService;

    public String multiply(BigDecimal price, Integer count) {
        return price.multiply(BigDecimal.valueOf(count)).toString();
    }

    public String summarizeAll(Map<Long, Integer> productIdAndCount) {
        String language = LocaleContextHolder.getLocale().getLanguage();
        boolean isEnglish = "en".equalsIgnoreCase(language);
        Product product;
        BigDecimal sum = BigDecimal.ZERO;
        if (isEnglish) {
            for (Map.Entry<Long, Integer> entry : productIdAndCount.entrySet()) {
                product = productService.findById(entry.getKey());
                BigDecimal productPrice = product.getPriceEU();
                sum = sum.add(productPrice.multiply(BigDecimal.valueOf(entry.getValue())));
            }
        } else {
            for (Map.Entry<Long, Integer> entry : productIdAndCount.entrySet()) {
                product = productService.findById(entry.getKey());
                BigDecimal productPrice = product.getPriceUA();
                sum = sum.add(productPrice.multiply(BigDecimal.valueOf(entry.getValue())));
            }
        }
        return sum.toString();
    }
}

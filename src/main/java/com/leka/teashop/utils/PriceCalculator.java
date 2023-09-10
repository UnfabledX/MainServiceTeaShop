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
        return getMultiplied(price, count).toString();
    }

    public String summarizeAll(Map<Long, Integer> productIdAndCount) {
        Product product;
        boolean isEnglish = isEnglish();
        BigDecimal sum = BigDecimal.ZERO;
        for (Map.Entry<Long, Integer> entry : productIdAndCount.entrySet()) {
            product = productService.findById(entry.getKey());
            BigDecimal productPrice = isEnglish ? product.getPriceEU() : product.getPriceUA();
            sum = sum.add(getMultiplied(productPrice, entry.getValue()));
        }
        return sum.toString();
    }

    private static boolean isEnglish() {
        String language = LocaleContextHolder.getLocale().getLanguage();
        return "en".equalsIgnoreCase(language);
    }

    private static BigDecimal getMultiplied(BigDecimal price, Integer count) {
        return price.multiply(BigDecimal.valueOf(count));
    }

}

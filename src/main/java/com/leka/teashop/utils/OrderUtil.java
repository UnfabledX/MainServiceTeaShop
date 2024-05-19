package com.leka.teashop.utils;

import com.leka.teashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import static com.leka.teashop.model.OrderStatus.IN_PROGRESS;

@Log4j2
@Component(value = "orderUtil")
@RequiredArgsConstructor
public class OrderUtil {

    private final OrderService orderService;

    public String countAllOrdersInProcess() {
        Integer count = 0;
        try {
            count = orderService.countOrdersByOrderStatus(IN_PROGRESS);
        } catch (WebClientResponseException e) {
            log.error("OrderService is unavailable ", e);
        }
        return String.valueOf(count);
    }
}

package com.leka.teashop.utils;

import com.leka.teashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.leka.teashop.model.OrderStatus.IN_PROGRESS;

@Component(value = "orderUtil")
@RequiredArgsConstructor
public class OrderUtil {

    private final OrderService orderService;

    public String countAllOrdersInProcess() {
        Integer count = orderService.countOrdersByOrderStatus(IN_PROGRESS);
        return String.valueOf(count);
    }
}

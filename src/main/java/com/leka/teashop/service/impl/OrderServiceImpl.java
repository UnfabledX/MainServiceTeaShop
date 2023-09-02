package com.leka.teashop.service.impl;

import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final WebClient orderWebClient;

    @Override
    public void addToOrder(User currentUser, String quantity, ProductDto productDto) {
        OrderDto currentOrder;
        if (currentUser.getCurrentOrderDto() == null) {
            currentOrder = OrderDto.builder()
                    .userId(currentUser.getId())
                    .productIdAndCount(new HashMap<>())
                    .orderStatus("STARTED").build();
        } else {
            currentOrder = currentUser.getCurrentOrderDto();
        }

        Map<Long, Integer> productIdAndCount = currentOrder.getProductIdAndCount();
        Long productId = productDto.getId();
        if (productIdAndCount.containsKey(productId)) {
            Integer addedValue = productIdAndCount.get(productId) + Integer.parseInt(quantity);
            productIdAndCount.put(productId, addedValue);
        } else {
            productIdAndCount.put(productId, Integer.parseInt(quantity));
        }
        OrderDto savedCurrentOrderWithId = orderWebClient.post()
                .uri("/api/v1/orders/save")
                .body(Mono.just(currentOrder), OrderDto.class)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
        currentUser.setCurrentOrderDto(savedCurrentOrderWithId);
    }
}

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
    private final String started = "STARTED";

    @Override
    public void addToOrder(User currentUser, String quantity, ProductDto productDto) {
        OrderDto currentOrder;
        if (currentUser.getCurrentOrderDto() == null) {
            currentOrder = OrderDto.builder()
                    .userId(currentUser.getId())
                    .productIdAndCount(new HashMap<>())
                    .orderStatus(started).build();
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

    @Override
    public void deleteStartedOrdersWhenLogout(User currentUser) {
        orderWebClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/delete")
                        .queryParam("id", currentUser.getId())
                        .queryParam("status", started)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public void decreaseCounter(Long productId, OrderDto order) {
        Map<Long, Integer> productIdAndCount = order.getProductIdAndCount();
        int decreasedCount = productIdAndCount.get(productId) - 1;
        if (decreasedCount == 0) {
            productIdAndCount.remove(productId);
        } else {
            productIdAndCount.put(productId, decreasedCount);
        }
        order.setProductIdAndCount(productIdAndCount);
    }

    @Override
    public void increaseCounter(Long productId, OrderDto order) {
        Map<Long, Integer> productIdAndCount = order.getProductIdAndCount();
        Integer increasedCount = productIdAndCount.get(productId) + 1;
        productIdAndCount.put(productId, increasedCount);
        order.setProductIdAndCount(productIdAndCount);
    }

    @Override
    public void deleteProductFromCart(Long productId, OrderDto order) {
        Map<Long, Integer> productIdAndCount = order.getProductIdAndCount();
        productIdAndCount.remove(productId);
        order.setProductIdAndCount(productIdAndCount);
    }


}

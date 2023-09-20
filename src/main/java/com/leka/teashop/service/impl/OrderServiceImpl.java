package com.leka.teashop.service.impl;

import com.leka.teashop.exception.NotFoundException;
import com.leka.teashop.model.User;
import com.leka.teashop.model.dto.OrderDto;
import com.leka.teashop.model.dto.ProductDto;
import com.leka.teashop.service.OrderService;
import com.leka.teashop.utils.WebClientPageImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
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
            deleteProductFromCart(productId, order);
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
        if (productIdAndCount.isEmpty()) {
            orderWebClient.delete()
                    .uri("/api/v1/orders/delete/{id}", order.getId())
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } else {
            order.setProductIdAndCount(productIdAndCount);
        }

    }

    @Override
    public void saveOrderWithStatus(OrderDto currentOrder, String orderStatus) {
        currentOrder.setOrderStatus(orderStatus);
        orderWebClient.post()
                .uri("/api/v1/orders/save")
                .body(Mono.just(currentOrder), OrderDto.class)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
    }

    @Override
    public void updateStartedOrderToActualState(OrderDto currentOrder) {
        orderWebClient.post()
                .uri("/api/v1/orders/save")
                .body(Mono.just(currentOrder), OrderDto.class)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
    }

    @Override
    public Page<OrderDto> getAllOrdersByUserId(Long userId, Integer pageNo, Integer pageSize,
                                               String sortField, String sortDirection) {
        return orderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/users/{userId}")
                        .queryParam("page", pageNo)
                        .queryParam("size", pageSize)
                        .queryParam("sort", sortField)
                        .queryParam("dir", sortDirection)
                        .build(userId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<WebClientPageImpl<OrderDto>>() {})
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Order service is unavailable"));
    }

    @Override
    public Page<OrderDto> getAllOrdersByStatus(String status, Integer pageNo, Integer pageSize,
                                               String sortField, String sortDirection) {
        return orderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/status/{status}")
                        .queryParam("page", pageNo)
                        .queryParam("size", pageSize)
                        .queryParam("sort", sortField)
                        .queryParam("dir", sortDirection)
                        .build(status))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<WebClientPageImpl<OrderDto>>() {})
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Order service is unavailable"));
    }

    @Override
    public Integer countOrdersByOrderStatus(String status) {
        return orderWebClient.get()
                .uri("/api/v1/orders/count/{status}", status)
                .retrieve()
                .bodyToMono(Integer.class)
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Order service is unavailable"));
    }

    @Override
    public Page<OrderDto> getAllOrders(Integer pageNo, Integer pageSize, String sortField, String sortDirection) {
        return orderWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders")
                        .queryParam("page", pageNo)
                        .queryParam("size", pageSize)
                        .queryParam("sort", sortField)
                        .queryParam("dir", sortDirection)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<WebClientPageImpl<OrderDto>>() {})
                .blockOptional()
                .orElseThrow(() -> new NotFoundException("Order service is unavailable"));
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderWebClient.get()
                .uri("/api/v1/orders/{orderId}", orderId)
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
    }

    @Override
    public OrderDto updateOrderByIdAndWithStatus(Long orderId, String orderStatus) {
        return orderWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/orders/update/{orderId}")
                        .queryParam("status", orderStatus)
                        .build(orderId))
                .retrieve()
                .bodyToMono(OrderDto.class)
                .block();
    }
}
